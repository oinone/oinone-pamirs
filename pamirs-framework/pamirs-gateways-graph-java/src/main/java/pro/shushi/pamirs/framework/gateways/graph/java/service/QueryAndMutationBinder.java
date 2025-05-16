package pro.shushi.pamirs.framework.gateways.graph.java.service;

import graphql.schema.AsyncDataFetcher;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.graph.configuration.PamirsFrameworkGatewayConfiguration;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.executor.ExecutorServiceApi;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.NullMarkUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.SessionExtendUtils;
import pro.shushi.pamirs.framework.gateways.graph.spi.ActionBinderApi;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.ReadApi;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientFieldConverter;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldQueryApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.model.RelatedValue;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.FieldAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.AttributesUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Optional;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * Query And Mutation 绑定器
 * <p>
 * 2020/10/20 10:52 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class QueryAndMutationBinder implements GraphQLSdlConstants {

    private static final HoldKeeper<Boolean> isAsync = new HoldKeeper<>();

    private static final HoldKeeper<ActionBinderApi> actionBinderApiHolder = new HoldKeeper<>();

    private static boolean isAsync() {
        return isAsync.supply(() -> BeanDefinitionUtils.getBean(PamirsFrameworkGatewayConfiguration.class).isAsync());
    }

    private static ActionBinderApi getActionBinderApi() {
        return actionBinderApiHolder.supply(() -> Spider.getDefaultExtension(ActionBinderApi.class));
    }

    public static void buildWiring(RuntimeWiring.Builder runtimeWiring, TypeRuntimeWiring.Builder rootTypeBuilder,
                                   ModelConfig modelConfig, boolean isQuery) {
        if (null == rootTypeBuilder) {
            return;
        }
        rootTypeBuilder.dataFetcher(modelConfig.getName() + (isQuery ? CAPITAL_QUERY : CAPITAL_MUTATION), dataFetcher -> {
            if (isQuery) {
                return CommonApiFactory.getApi(ReadApi.class);
            } else {
                return CommonApiFactory.getApi(WriteWithFieldApi.class);
            }
        });
        // 绑定函数
        TypeRuntimeWiring.Builder queryAndMutationTypeBuilder = buildQueryOrMutation(modelConfig, isQuery);
        runtimeWiring.type(queryAndMutationTypeBuilder);
    }

    public static TypeRuntimeWiring.Builder buildFieldRelation(ModelConfig modelConfig) {
        TypeRuntimeWiring.Builder typeBuilder = newTypeWiring(StringUtils.capitalize(modelConfig.getName()));
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            Optional.ofNullable(dataFetcher(modelFieldConfig)).ifPresent(v -> typeBuilder.dataFetcher(modelFieldConfig.getName(), v));
        }
        return typeBuilder;
    }

    /**
     * 获取模型root的处理器
     *
     * @param isQuery 是否查询请求
     * @return 数据获取器
     */
    public static DataFetcher<?> wiringDataFetcher(Boolean isQuery) {
        return dataFetcher -> {
            if (isQuery) {
                return CommonApiFactory.getApi(ReadApi.class);
            } else {
                return CommonApiFactory.getApi(WriteWithFieldApi.class);
            }
        };
    }

    /**
     * 获取函数的处理器
     *
     * @param function    函数配置
     * @param modelConfig 模型配置
     * @return 数据获取器
     */
    public static DataFetcher<?> dataFetcher(Function function, ModelConfig modelConfig) {
        if (isAsync()) {
            if (FunctionTypeEnum.QUERY.in(function.getType())) {
                return AsyncDataFetcher.async(dataFetchingEnvironment -> dataFetcherAction(function, modelConfig, dataFetchingEnvironment), ExecutorServiceApi.getExecutorService());
            } else {
                return dataFetchingEnvironment -> dataFetcherAction(function, modelConfig, dataFetchingEnvironment);
            }
        } else {
            return dataFetchingEnvironment -> dataFetcherAction(function, modelConfig, dataFetchingEnvironment);
        }
    }

    private static Object dataFetcherAction(Function function, ModelConfig modelConfig, DataFetchingEnvironment environment) {
        try {
            SessionExtendUtils.tagMainRequest();
            // 使用共享的请求和响应对象
            return getActionBinderApi().action(modelConfig, function, FunctionTypeEnum.QUERY.in(function.getType()), environment);
        } finally {
            PamirsSession.clearMainSession();
        }
    }

    /**
     * 获取字段的处理器
     *
     * @param modelFieldConfig 字段配置
     * @return 数据获取器
     */
    public static DataFetcher<?> dataFetcher(ModelFieldConfig modelFieldConfig) {
        String references = modelFieldConfig.getReferences();
        if (TtypeEnum.isRelatedType(modelFieldConfig.getTtype())) {
            // 交给客户端协议支持处理，将引用字段转化为关系字段与普通字段的查询
            return dataFetchingEnvironment -> { // TODO 此逻辑待锦帆前端实现后去掉
                try {
                    Object source = dataFetchingEnvironment.getSource();
                    if (null == source) {
                        return null;
                    }

                    // Null标记处理
                    Object nullMark = NullMarkUtils.handleDataFetchingEnvironmentNullMark(dataFetchingEnvironment, modelFieldConfig);
                    if (nullMark != null) {
                        return nullMark;
                    }

                    RelatedValue relatedValueResult = Models.directive().request(() -> CommonApiFactory.getApi(RelatedFieldQueryApi.class).queryRelated(modelFieldConfig, source));
                    Object result = relatedValueResult.getRelatedValue();
                    // 前后端字段适配
                    if (StringUtils.isBlank(references)) {
                        result = Spider.getDefaultExtension(ClientFieldConverter.class).out(modelFieldConfig, result);
                    } else {
                        result = ClientDataConverter.get().out(references, result);
                    }
                    FieldUtils.setFieldValue(source, modelFieldConfig.getLname(), result);
                    return result;
                } finally {
                    PamirsSession.clearSubSession();
                }
            };
        } else if (!TtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
            return dataFetchingEnvironment -> {
                Object source = dataFetchingEnvironment.getSource();
                if (null == source) {
                    return null;
                }
                // Null标记处理
                Object nullMark = NullMarkUtils.handleDataFetchingEnvironmentNullMark(dataFetchingEnvironment, modelFieldConfig);
                if (nullMark != null) {
                    return nullMark;
                }

                return FieldUtils.getFieldValue(dataFetchingEnvironment.getSource(), modelFieldConfig.getLname());
            };
        }
        if (StringUtils.isBlank(references)) {
            return null;
        }
        ModelConfig referenceModel = PamirsSession.getContext().getSimpleModelConfig(references);
        if (null == referenceModel) {
            return null;
        }
        // 关系字段查询
        if ((ModelTypeEnum.STORE.equals(referenceModel.getType())
                || ModelTypeEnum.PROXY.equals(referenceModel.getType())) && modelFieldConfig.getRelationStore()) {
            Boolean page = AttributesUtils.get(modelFieldConfig.getModelField(), ModelField::getAttributes, FieldAttributeConstants.PAGE);
            if (TtypeEnum.isRelationMany(modelFieldConfig.getTtype()) && Boolean.TRUE.equals(page)) {
                return null;
            }
            if (StringUtils.isNotBlank(modelFieldConfig.getDomain()) || StringUtils.isNotBlank(modelFieldConfig.getSearch())) {
                if (isAsync()) {
                    return AsyncDataFetcher.async(dataFetchingEnvironment -> dataFetcherRelationQuery(modelFieldConfig, dataFetchingEnvironment), ExecutorServiceApi.getExecutorService());
                } else {
                    return dataFetchingEnvironment -> dataFetcherRelationQuery(modelFieldConfig, dataFetchingEnvironment);
                }
            } else {
                return dataFetchingEnvironment -> getActionBinderApi().batchRelationQuery(modelFieldConfig, dataFetchingEnvironment);
            }
        }

        return dataFetchingEnvironment -> {
            Object source = dataFetchingEnvironment.getSource();
            if (null == source) {
                return null;
            }
            // Null标记处理
            Object nullMark = NullMarkUtils.handleDataFetchingEnvironmentNullMark(dataFetchingEnvironment, modelFieldConfig);
            if (nullMark != null) {
                return nullMark;
            }

            return FieldUtils.getFieldValue(dataFetchingEnvironment.getSource(), modelFieldConfig.getLname());
        };
    }

    private static Object dataFetcherRelationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment environment) {
        try {
            Object source = environment.getSource();
            if (source == null) {
                return null;
            }
            return getActionBinderApi().relationQuery(modelFieldConfig, environment);
        } finally {
            PamirsSession.clearSubSession();
        }
    }

    private static TypeRuntimeWiring.Builder buildQueryOrMutation(ModelConfig modelConfig, boolean isQuery) {
        TypeRuntimeWiring.Builder typeBuilder = newTypeWiring(StringUtils.capitalize(modelConfig.getName()) + (isQuery ? CAPITAL_QUERY : CAPITAL_MUTATION));
        for (Function function : modelConfig.getFunctionList()) {
            if ((FunctionTypeEnum.QUERY.in(function.getType())) == isQuery) {
                typeBuilder.dataFetcher(function.getName(), dataFetchingEnvironment -> {
                    try {
                        SessionExtendUtils.tagMainRequest();
                        return getActionBinderApi().action(modelConfig, function, isQuery, dataFetchingEnvironment);
                    } finally {
                        PamirsSession.clearMainSession();
                    }
                });
            }
        }
        return typeBuilder;
    }

}

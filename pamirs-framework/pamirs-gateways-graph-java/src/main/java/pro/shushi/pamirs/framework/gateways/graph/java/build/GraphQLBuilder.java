package pro.shushi.pamirs.framework.gateways.graph.java.build;

import graphql.Scalars;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.graph.PamirsRuntimeWiring;
import pro.shushi.pamirs.framework.gateways.graph.coercing.*;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.request.DefaultRequestExecutor;
import pro.shushi.pamirs.framework.gateways.graph.java.service.*;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.fetcher.PamirsDefaultDataFetcherFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForKeySet;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate.BASE_SDL_ERROR;
import static pro.shushi.pamirs.framework.gateways.graph.enmu.GqlExpEnumerate.BASE_SDL_MERGE_ERROR;

/**
 * GraphQL构造器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
@Slf4j
public class GraphQLBuilder implements GraphQLSdlConstants {

    public static void build(Set<String> runModuleSet) {
        build(runModuleSet, false);
    }

    public static void build(Set<String> runModuleSet, boolean showDoc) {
        GraphQLSchema graphQLSchema = buildGraphQLSchema(runModuleSet, showDoc);
        DefaultRequestExecutor.addSchema(graphQLSchema);
    }

    public static GraphQLSchema buildGraphQLSchema(Set<String> runModuleSet, boolean showDoc) {
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        RuntimeWiring.Builder runtimeWiring = PamirsRuntimeWiring.newRuntimeWiring();
        GraphQLCodeRegistry.Builder codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
                .defaultDataFetcher(PamirsDefaultDataFetcherFactory.INSTANCE);
        runtimeWiring.codeRegistry(codeRegistry);

        Result<String> result = buildSdl(runtimeWiring, runModuleSet);
        if (!result.isSuccess()) {
            throw PamirsException.construct(BASE_SDL_ERROR)
                    .appendMsg("dsl:" + result.getData()).errThrow();
        }
        try {
            typeRegistry.merge(new SchemaParser().parse(result.getData()));
        } catch (Exception e) {
            throw PamirsException.construct(BASE_SDL_MERGE_ERROR, e)
                    .appendMsg("dsl:" + result.getData()).errThrow();
        }
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        if (!showDoc) {
            runtimeWiring.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY);
        }

        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring.build());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Result<String> buildSdl(RuntimeWiring.Builder runtimeWiring, Set<String> runModuleSet) {
        Result result = new Result<>();

        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder mutationBuilder = new StringBuilder();
        StringBuilder modelGraphqlTypeSb = new StringBuilder();
        RequestContext requestContext = PamirsSession.getContext();
        modelGraphqlTypeSb
                .append(SCALAR).append(StringUtils.SPACE).append(Scalars.GraphQLByte.getName()).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(Scalars.GraphQLShort.getName()).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(LongScalarCoercing.GraphQLLong).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(BigDecimalScalarCoercing.GraphQLBIGDECIMAL).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(MoneyScalarCoercing.GraphQLMoney).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(DateScalarCoercing.GraphQLDate).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(HtmlScalarCoercing.GraphQLHtml).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(Object.class.getSimpleName()).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(MapScalarCoercing.GraphQLMap).append(StringUtils.LF)
                .append(SCALAR).append(StringUtils.SPACE).append(VoidScalarCoercing.GraphQLVoid).append(StringUtils.LF)
        ;

        Set<String> modelSet = ((SessionCacheForKeySet) Objects.requireNonNull(requestContext).getModelCache()).keySet();

        // 数据绑定
        TypeRuntimeWiring.Builder queryTypeBuilder = null;
        TypeRuntimeWiring.Builder mutationTypeBuilder = null;
        if (null != runtimeWiring) {
            queryTypeBuilder = newTypeWiring(QUERY_TYPE);
            mutationTypeBuilder = newTypeWiring(MUTATION_TYPE);
        }
        GraphQLVerifyContext verifyContext = new GraphQLVerifyContext();
        // 模型、字段、函数
        for (String model : modelSet) {
            ModelConfig modelConfig = requestContext.getModelConfig(model);

            if (modelConfig == null || ModelTypeEnum.ABSTRACT.value().equals(modelConfig.getType().value())
                    || IWrapper.MODEL_MODEL.equals(modelConfig.getModel())) {
                continue;
            }

            // 类型定义
            ModelGenerator.generate(modelGraphqlTypeSb, requestContext, modelConfig, null, Boolean.FALSE);

            // 入参定义
            ModelGenerator.generate(modelGraphqlTypeSb, requestContext, modelConfig, null, Boolean.TRUE);

            // 分页对象与条件更新对象定义
            if (null != modelConfig.isDataManager() && modelConfig.isDataManager()) {
                ModelGenerator.generate(modelGraphqlTypeSb, requestContext, modelConfig, Pagination.MODEL_MODEL, Boolean.FALSE);
                ModelGenerator.generate(modelGraphqlTypeSb, requestContext, modelConfig, IWrapper.MODEL_MODEL, Boolean.TRUE);
            }

            // 绑定关联关系
            TypeRuntimeWiring.Builder relationTypeBuilder = QueryAndMutationBinder.buildFieldRelation(modelConfig);
            if (null != runtimeWiring) {
                runtimeWiring.type(relationTypeBuilder);
            }

            // 添加函数定义
            FunctionGenerator.generate(modelGraphqlTypeSb, requestContext, runModuleSet, modelConfig);

            // 生成query和mutation
            QueryAndMutationGenerator.generate(queryBuilder, mutationBuilder, runModuleSet, modelConfig);

            // 数据方法绑定
            if (null != runtimeWiring) {
                QueryAndMutationBinder.buildWiring(runtimeWiring, queryTypeBuilder, modelConfig, true);
                QueryAndMutationBinder.buildWiring(runtimeWiring, mutationTypeBuilder, modelConfig, false);
                runtimeWiring.type(queryTypeBuilder);
                runtimeWiring.type(mutationTypeBuilder);
            }

        }
        // 枚举
        for (String dictionary : ((SessionCacheForKeySet) requestContext.getDictCache()).keySet()) {
            DataDictionary dataDictionary = requestContext.getDictionary(dictionary);
            if (dataDictionary == null) {
                log.error("data dictionary is lost. {}", dictionary);
                continue;
            }
            EnumGenerator.generate(verifyContext, modelGraphqlTypeSb, dataDictionary);
        }
        DslRootGenerator.generate(modelGraphqlTypeSb, queryBuilder, mutationBuilder);

        List<String> errors = verifyContext.getErrors();
        if (!errors.isEmpty()) {
            printErrors(errors);
            throw new IllegalArgumentException("请根据错误提示进行纠正");
        }

        return result.setData(modelGraphqlTypeSb.toString());
    }

    private static void printErrors(List<String> errors) {
        for (String error : errors) {
            log.error(error);
        }
    }

}

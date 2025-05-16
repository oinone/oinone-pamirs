package pro.shushi.pamirs.framework.gateways.graph.java.action;

import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.utils.NullMarkUtils;
import pro.shushi.pamirs.framework.gateways.graph.spi.ActionBinderApi;
import pro.shushi.pamirs.framework.gateways.graph.spi.DataLoaderRegistryApi;
import pro.shushi.pamirs.framework.gateways.graph.util.ActionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.audit.spi.DataAuditApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientArgumentHandlerApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationReadApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.relation.RelationKey;
import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 请求控制器实现
 * 2021/3/29 3:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
@Slf4j
public class DefaultActionBinderApi implements ActionBinderApi {

    @Resource
    private RelationReadApi relationManagerProcessor;

    @Override
    public Object action(ModelConfig modelConfig, Function function, boolean isQuery, DataFetchingEnvironment env) {
        ClientArgumentHandlerApi argumentHandlerApi = Spider.getDefaultExtension(ClientArgumentHandlerApi.class);
        Object result;
        Object[] args;
        if (CollectionUtils.isEmpty(function.getArguments())) {
            result = Models.directive().request(() -> Fun.run(function));
        } else {
            args = new Object[function.getArguments().size()];
            result = Models.directive().request(() -> {
                if (isQuery) {
                    PamirsSession.directive().disableCheck();
                }
                Map<String, Object> requestArgs = env.getArguments();
                argumentHandlerApi.in(modelConfig, function, isQuery, requestArgs, args);
                if (PamirsSession.getRequestVariables().getRequestInfo().isOnlyValidate()) {
                    return null;
                }
                if (ActionUtils.isBuiltAction(function.getNamespace(), function.getFun())) {
                    // 默认未被重写的Function，才会保存模型字段
                    if ("defaultWriteWithFieldApi".equals(function.getBeanName())) {
                        PamirsSession.directive().enableBuiltAction();
                    }
                }

                // 数据审计入口
                String traceId = PamirsSession.getRequestVariables().getTraceId();
                if (log.isDebugEnabled()) {
                    log.debug("http-request-traceId: {}", traceId);
                }
                Spider.getDefaultExtension(DataAuditApi.class).computeDataAuditSession(UriType.HTTP, function.getNamespace(), function.getFun(), traceId);

                return Fun.run(function, args);
            });
        }
        if (null == result) {
            return null;
        } else if (result.getClass().isArray()) {
            return FunctionUtils.fetchDynamicParameter(result, ((Object[]) result).length);
        } else {
            return result;
        }
    }

    @Override
    public Object relationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment env) {
        return relationQuery(modelFieldConfig, env, source -> {
            Object result = Models.directive().request(() ->
                    relationManagerProcessor.queryFieldByRelation(modelFieldConfig, source));
            FieldUtils.setFieldValue(source, modelFieldConfig.getLname(), result);
            return result;
        });
    }

    @Override
    public Object batchRelationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment env) {
        return relationQuery(modelFieldConfig, env, source -> {
            String key = RelationKey.key(modelFieldConfig, source);
            if (StringUtils.isBlank(key)) {
                return null;
            }
            RelationKey relationKey = RelationKey.init(modelFieldConfig, source);
            return env.getDataLoader(DataLoaderRegistryApi.COMMON_DATA_LOADER).load(key, relationKey);
        });
    }

    private Object relationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment env,
                                 java.util.function.Function<Object, Object> supplier) {
        Object source = env.getSource();
        if (null == source) {
            return null;
        }

        // Null标记处理
        Object nullMark = NullMarkUtils.handleDataFetchingEnvironmentNullMark(env, modelFieldConfig);
        if (nullMark != null) {
            return nullMark;
        }

        Object result = FieldUtils.getFieldValue(source, modelFieldConfig.getLname());
        if (relationManagerProcessor.isNeedQueryRelation(modelFieldConfig, result)) {
            String key = RelationKey.key(modelFieldConfig, source);
            if (StringUtils.isBlank(key)) {
                return null;
            }
            return supplier.apply(source);
        }
        return result;
    }

    @Override
    public List<Object> relationQuery(List<String> keys, BatchLoaderEnvironment env) {
        return relationManagerProcessor.listFieldQueryByRelationKey(keys, env.getKeyContexts());
    }

}

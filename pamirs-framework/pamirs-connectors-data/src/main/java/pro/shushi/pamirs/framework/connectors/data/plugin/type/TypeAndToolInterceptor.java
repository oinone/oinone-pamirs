package pro.shushi.pamirs.framework.connectors.data.plugin.type;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.Map;

/**
 * 工具支持
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 2:11 上午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class TypeAndToolInterceptor implements Interceptor {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            map.put(FieldUtil.NAME, FieldUtil.INSTANCE);

            // 获取配置信息
            String model = MapperContext.model(map);
            if (StringUtils.isBlank(model)) {
                return invocation.proceed();
            }
            ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
            if (null == modelConfig) {
                return invocation.proceed();
            }
            if (modelConfig.isStaticConfig()) {
                PamirsSession.setStaticConfig(true);
            }
            Object result = invocation.proceed();
            PamirsSession.setStaticConfig(false);
            return result;
        }
        return invocation.proceed();
    }

}

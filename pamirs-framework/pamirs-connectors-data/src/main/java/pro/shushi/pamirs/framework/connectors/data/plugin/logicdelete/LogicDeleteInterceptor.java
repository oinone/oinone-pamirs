package pro.shushi.pamirs.framework.connectors.data.plugin.logicdelete;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.util.LogicDeleteUtils;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;

import java.util.Map;
import java.util.Properties;

/**
 * 逻辑删除支持
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 2:11 上午
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class LogicDeleteInterceptor implements Interceptor {

    @Override
    @SuppressWarnings({"rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.DELETE != ms.getSqlCommandType()
                && SqlCommandType.SELECT != ms.getSqlCommandType()
                && SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            // 获取配置信息
            String model = MapperContext.model(map);
            if (StringUtils.isBlank(model)) {
                return invocation.proceed();
            }
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            boolean logicDelete = pamirsTableInfo.getLogicDelete();
            if (!logicDelete) {
                return invocation.proceed();
            }
            LogicDeleteUtils.fillLogicDelete(pamirsTableInfo, map);
            return invocation.proceed();
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

}

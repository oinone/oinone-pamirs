package pro.shushi.pamirs.framework.connectors.data.plugin.sql;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 防止全表更新与删除
 */
@Data
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SqlExplainInterceptor extends JsqlParserSupport implements Interceptor {

    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(SqlExplainInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (ms.getSqlCommandType() == SqlCommandType.DELETE || ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            Object parameter = args[1];

            StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
            PluginUtils.MPStatementHandler handler = PluginUtils.mpStatementHandler(statementHandler);
            parserMulti(handler.boundSql().getSql(), parameter);
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
}

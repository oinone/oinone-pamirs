package pro.shushi.pamirs.framework.connectors.data.plugin.sql;

import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 防止全表更新与删除
 */
@Data
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SqlExplainInterceptor extends AbstractSqlParserHandler implements Interceptor {

    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(SqlExplainInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (ms.getSqlCommandType() == SqlCommandType.DELETE || ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            Object parameter = args[1];
            Configuration configuration = ms.getConfiguration();
            Object target = invocation.getTarget();
            StatementHandler handler = configuration.newStatementHandler((Executor) target, ms, parameter, RowBounds.DEFAULT, null, null);
            this.sqlParser(SystemMetaObject.forObject(handler));
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

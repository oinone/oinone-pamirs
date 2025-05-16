package pro.shushi.pamirs.framework.connectors.data.plugin.page;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/13 9:53 下午
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class})})
public class PageLocalCacheDisableInterceptor implements Interceptor {

    private static final String DEFAULT_PAGE_SQLID = ".*Page$";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (ms.getId().matches(DEFAULT_PAGE_SQLID)) {
            Class<?> clazz = ms.getClass();
            Field flushLocalCache = clazz.getDeclaredField("flushCacheRequired");
            flushLocalCache.setAccessible(true);
            flushLocalCache.set(ms, true);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //do nothing
    }

}

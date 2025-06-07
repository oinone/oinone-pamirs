package pro.shushi.pamirs.record.sql.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.record.sql.manager.SQLRecordQueueManager;
import pro.shushi.pamirs.record.sql.manager.SQLRecordSessionManager;
import pro.shushi.pamirs.record.sql.pojo.SQLRecord;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * SQLUpdateInterceptor
 *
 * @author yakir on 2023/06/28 10:52.
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "commit", args = {boolean.class}),
})
@Component
@Order
public class SQLRecordInterceptor implements Interceptor {

    @Autowired
    private SQLRecordSessionManager sqlRecordSessionManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        try {
            List<SQLRecord> sqlRecordList = sqlRecordSessionManager.get();
            if (null == sqlRecordList || sqlRecordList.isEmpty()) {
                return result;
            }

            for (SQLRecord sqlRecord : sqlRecordList) {
                sqlRecord.setcT(new Date());
                switch (sqlRecord.getFilterType()) {
                    case CHANGE_DATA:
                        SQLRecordQueueManager.get().changeDataPut(sqlRecord);
                        break;
                    case BINLOG_EVENT:
                        SQLRecordQueueManager.get().binlogEventPut(sqlRecord);
                        break;
                    case ALL:
                        SQLRecordQueueManager.get().changeDataPut(sqlRecord);
                        SQLRecordQueueManager.get().binlogEventPut(sqlRecord);
                        break;
                    default:
                        log.error("no match FilterType");
                        break;
                }
            }
        } catch (Throwable exp) {
            log.error("获取SQL上下文异常", exp);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}

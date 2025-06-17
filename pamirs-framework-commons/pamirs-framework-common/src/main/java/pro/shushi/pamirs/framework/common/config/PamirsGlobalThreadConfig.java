package pro.shushi.pamirs.framework.common.config;

import pro.shushi.pamirs.meta.util.PropertyHelper;

/**
 * Pamirs全局线程配置
 *
 * @author Adamancy Zhang at 12:49 on 2024-07-11
 */
public class PamirsGlobalThreadConfig {

    public static final int GLOBAL_ASYNC_EXECUTOR_THREAD_COUNT;

    public static final int GLOBAL_TTL_ASYNC_EXECUTOR_THREAD_COUNT;

    public static final int GLOBAL_RELATION_QUERY_ASYNC_EXECUTOR_THREAD_COUNT;

    static {
        GLOBAL_ASYNC_EXECUTOR_THREAD_COUNT = PropertyHelper.getIntProperty("pamirs.global.async.thread-count", 8);
        GLOBAL_TTL_ASYNC_EXECUTOR_THREAD_COUNT = PropertyHelper.getIntProperty("pamirs.global.async.ttl.thread-count", 32);
        GLOBAL_RELATION_QUERY_ASYNC_EXECUTOR_THREAD_COUNT = PropertyHelper.getIntProperty("pamirs.global.async.relation-query.thread-count", 32);
    }
}

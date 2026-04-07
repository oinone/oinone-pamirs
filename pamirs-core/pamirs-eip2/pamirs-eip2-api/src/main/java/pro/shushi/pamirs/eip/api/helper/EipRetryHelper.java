package pro.shushi.pamirs.eip.api.helper;

import pro.shushi.pamirs.eip.api.model.EipLog;

/**
 * Helper to propagate the original EipLog context via ThreadLocal during retries.
 *
 * @author yeshenyue on 2026/4/3 17:40.
 */
public class EipRetryHelper {

    private static final ThreadLocal<EipLog> RETRY_LOG_HOLDER = new ThreadLocal<>();

    public static void markRetry(EipLog originalLog) {
        RETRY_LOG_HOLDER.set(originalLog);
    }

    public static EipLog getRetryLog() {
        return RETRY_LOG_HOLDER.get();
    }

    public static boolean isRetrying() {
        return RETRY_LOG_HOLDER.get() != null;
    }

    public static void clearRetry() {
        RETRY_LOG_HOLDER.remove();
    }
}

package pro.shushi.pamirs.eip.api.constant;

/**
 * 熔断器常量
 * @author yeshenyue on 2025/4/16 10:12.
 */
public class EipCircuitBreakerConstant {

    // 熔断名称正则
    public static final String RULE_NAME_REGULAR = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]{2,50}$";
    // 统计时长最大值
    public static final Integer STATISTICAL_DURATION_MAX = 7200;

    // 缓存前缀
    public static final String CACHE_PREFIX = "pamirs:eip:cb:";
    // 熔断记录-list结构-异步落库
    public static final String CHANNEL_EVENTS = CACHE_PREFIX + "events";
    // 熔断时间
    private static final String KEY_STATE_PREFIX = CACHE_PREFIX + "state:%s";
    // 统计时间内总调用条数
    private static final String KEY_COUNT_TOTAL = CACHE_PREFIX + "count:total:%s";
    // 统计时间内失败调用条数
    private static final String KEY_COUNT_FAIL = CACHE_PREFIX + "count:fail:%s";
    // 统计时间内慢调用条数
    private static final String KEY_COUNT_SLOW = CACHE_PREFIX + "count:slow:%s";
    // 同步熔断记录锁
    public static final String SYNC_SAVE_RECORD_LOCK = CACHE_PREFIX + "record:lock";

    public static String getStateKey(String interfaceName) {
        return String.format(KEY_STATE_PREFIX, interfaceName);
    }

    public static String getCountTotalKey(String interfaceName) {
        return String.format(KEY_COUNT_TOTAL, interfaceName);
    }

    public static String getCountFailKey(String interfaceName) {
        return String.format(KEY_COUNT_FAIL, interfaceName);
    }

    public static String getCountSlowKey(String interfaceName) {
        return String.format(KEY_COUNT_SLOW, interfaceName);
    }
}

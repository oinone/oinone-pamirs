package pro.shushi.pamirs.eip.api.constant;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

/**
 * @author yeshenyue on 2025/4/10 14:14.
 */
public enum EipLogCountCacheConstant {

    // 成功调用
    SUCCESS("success"),

    // 失败调用
    FAIL("fail"),

    // 小于100ms调用次数
    ULTRA_FAST("ultra_fast"),

    // 100-300ms调用数量（左闭右开）
    VERY_FAST("very_fast"),

    // 300-500ms调用数量
    FAST("fast"),

    // 500-1000ms调用数量
    MODERATE("moderate"),

    // 1s-3s调用数量
    SLOW("slow"),

    // 3s-8s调用数量
    VERY_SLOW("very_slow"),

    // 8s-30s调用数量
    SLOWEST("slowest"),

    // 大于30s调用数量
    TIMEOUT("timeout");

    private final String keyPrefix;

    private static final String BASE_KEY = "pamirs:eip:log:count:";

    EipLogCountCacheConstant(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix(InterfaceTypeEnum interfaceType, String interfaceName) {
        return BASE_KEY + keyPrefix + ":" + interfaceType.getValue() + ":" + interfaceName;
    }
}

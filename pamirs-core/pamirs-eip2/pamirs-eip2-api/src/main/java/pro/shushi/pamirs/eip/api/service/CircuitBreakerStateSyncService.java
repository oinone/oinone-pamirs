package pro.shushi.pamirs.eip.api.service;

import pro.shushi.pamirs.eip.api.enmu.CircuitBreakerStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Arrays;
import java.util.function.BiPredicate;

/**
 * 熔断器状态同步服务
 *
 * @author yeshenyue on 2025/4/16 15:09.
 */
public interface CircuitBreakerStateSyncService {

    String CB_ZK_ROOT_PATH = "/eip_circuit_breaker";
    byte[] OPEN = new byte[]{0};
    byte[] CLOSED = new byte[]{1};
    byte[] HALF_OPEN = new byte[]{2};

    BiPredicate<byte[], byte[]> DEFAULT_COMPARATOR = (originData, data) -> {
        if (originData == null) {
            return Boolean.TRUE;
        }
        if (data == null) {
            return Boolean.FALSE;
        }
        if (originData.length == 1) {
            if (originData[0] == data[0]) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.TRUE;
        }
    };

    /**
     * 初始化监听
     */
    void startListener();

    /**
     * 刷新熔断器
     */
    void syncState(String interfaceName, CircuitBreakerStatusEnum statusEnum);

    /**
     * 注销熔断器
     */
    void removeStateNode(String interfaceName);

    static byte[] convertStatus(CircuitBreakerStatusEnum status) {
        if (CircuitBreakerStatusEnum.OPEN.equals(status)) {
            return OPEN;
        } else if (CircuitBreakerStatusEnum.CLOSED.equals(status)) {
            return CLOSED;
        } else if (CircuitBreakerStatusEnum.HALF_OPEN.equals(status)) {
            return HALF_OPEN;
        }
        throw PamirsException.construct(EipExpEnumerate.EIP_CB_NOT_STATUS).errThrow();
    }

    static CircuitBreakerStatusEnum parseStatus(byte[] byteDate) {
        if (Arrays.equals(byteDate, OPEN)) {
            return CircuitBreakerStatusEnum.OPEN;
        } else if (Arrays.equals(byteDate, CLOSED)) {
            return CircuitBreakerStatusEnum.CLOSED;
        } else if (Arrays.equals(byteDate, HALF_OPEN)) {
            return CircuitBreakerStatusEnum.HALF_OPEN;
        }
        throw PamirsException.construct(EipExpEnumerate.EIP_CB_NOT_STATUS).errThrow();
    }
}

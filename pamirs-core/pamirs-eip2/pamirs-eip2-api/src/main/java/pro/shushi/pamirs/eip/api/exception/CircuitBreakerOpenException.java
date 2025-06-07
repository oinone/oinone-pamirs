package pro.shushi.pamirs.eip.api.exception;

import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * @author yeshenyue on 2025/4/15 17:08.
 */
public class CircuitBreakerOpenException extends RuntimeException {
    private static final long serialVersionUID = -4190658032225745910L;
    public static final String ERROR_CODE = String.valueOf(EipExpEnumerate.EIP_CB_STATUS_OPEN.getCode());

    public CircuitBreakerOpenException(ExpBaseEnum expBaseEnum, String interfaceName) {
        super(expBaseEnum.msg() + ":" + interfaceName);
    }
}

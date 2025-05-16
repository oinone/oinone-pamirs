package pro.shushi.pamirs.framework.connectors.event.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * EventExpEnum
 *
 * @author yakir on 2023/12/22 19:32.
 */
@Errors(displayName = "消息系统错误枚举")
public enum EventExpEnum implements ExpBaseEnum, IEnum<Integer> {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10043000, "系统异常");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    EventExpEnum(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}

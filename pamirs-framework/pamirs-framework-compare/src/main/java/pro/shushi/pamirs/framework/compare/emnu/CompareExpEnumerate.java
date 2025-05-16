package pro.shushi.pamirs.framework.compare.emnu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统差量计算错误枚举")
public enum CompareExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10007000, "系统异常"), BASE_FUNCTION_NOT_EXIST_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10007001, "配置错误，函数不存在");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    CompareExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}

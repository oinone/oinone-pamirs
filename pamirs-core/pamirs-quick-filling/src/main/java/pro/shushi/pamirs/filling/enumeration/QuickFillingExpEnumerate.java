package pro.shushi.pamirs.filling.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 快速填报异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
@Errors(displayName = "快速填报模块错误枚举")
public enum QuickFillingExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10068000, "系统异常"),
    MODEL_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10068001, "模型找不到"),
    FIELD_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10068002, "字段找不到"),
    FIELD_VALIDATE_REQUIRED_ERROR(ERROR_TYPE.BIZ_ERROR, 10068003, "必填"),
    CONVERT_ERROR(ERROR_TYPE.BIZ_ERROR, 10068004, "数据不符合规则，请修改后继续"),
    ;

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    QuickFillingExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public ERROR_TYPE type() {
        return type;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }

}

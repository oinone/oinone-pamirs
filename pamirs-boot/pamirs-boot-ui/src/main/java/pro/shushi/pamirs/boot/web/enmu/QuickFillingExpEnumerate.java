package pro.shushi.pamirs.boot.web.enmu;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 快速填报异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
public enum QuickFillingExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10068000, "系统异常"),
    MODEL_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10068001, "模型找不到"),
    FIELD_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10068002, "字段找不到"),
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

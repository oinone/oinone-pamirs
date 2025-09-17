package pro.shushi.pamirs.draft.api.enums;

import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 草稿异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
public enum DraftExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10065000, "系统异常"),
    USER_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10065001, "未找到用户信息"),
    MODEL_NOT_FIND(ERROR_TYPE.SYSTEM_ERROR, 10065002, "未找到模型信息"),
    ;

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    DraftExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

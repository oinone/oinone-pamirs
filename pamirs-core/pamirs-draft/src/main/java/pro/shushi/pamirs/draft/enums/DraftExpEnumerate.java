package pro.shushi.pamirs.draft.enums;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 草稿异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
@Errors(displayName = "草稿模块错误枚举")
public enum DraftExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10080000, "系统异常"),
    DRAFT_CODE_IS_NULL(ERROR_TYPE.BIZ_ERROR, 10080001, "草稿编码不允许为空"),
    DRAFT_MODEL_IS_NULL(ERROR_TYPE.BIZ_ERROR, 10080002, "草稿模型不允许为空"),
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

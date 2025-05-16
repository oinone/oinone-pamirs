package pro.shushi.pamirs.boot.web.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 分享异常枚举
 *
 * @author Adamancy Zhang at 15:50 on 2024-04-12
 */
@Errors(displayName = "分享异常枚举")
public enum SharedExpEnumerate implements ExpBaseEnum {

    INVALID_SHARED_ORIGIN(ERROR_TYPE.SYSTEM_ERROR, 10045000, "无效的URL参数"),
    INVALID_SHARED_PARAMETERS(ERROR_TYPE.SYSTEM_ERROR, 10045001, "无效的分享参数"),
    INVALID_SHARED_ACTION_PARAMETERS(ERROR_TYPE.SYSTEM_ERROR, 10045002, "无效的动作参数"),
    INVALID_SHARED_ACTION(ERROR_TYPE.SYSTEM_ERROR, 10045003, "分享的动作不存在"),
    INVALID_SHARED_CODE(ERROR_TYPE.SYSTEM_ERROR, 10045004, "无效的分享码"),
    INVALID_SHARED_PAGE(ERROR_TYPE.SYSTEM_ERROR, 10045005, "分享的页面不存在");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    SharedExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Base
@Errors(displayName = "基础错误枚举")
public enum BaseExpEnumerate implements ExpBaseEnum {

    BASE_USER_NOT_LOGIN_ERROR(ERROR_TYPE.BIZ_ERROR, 11500001, "用户未登录"),
    BASE_HOME_IS_NOT_EXISTS_ERROR(ERROR_TYPE.BIZ_ERROR, 11500002, "未找到入口应用或无权限访问"),
    BASE_HOME_PAGE_IS_NOT_EXISTS_ERROR(ERROR_TYPE.BIZ_ERROR, 11500003, "未找到首页"),
    BASE_PAGE_IS_NOT_EXISTS_ERROR(ERROR_TYPE.BIZ_ERROR, 10021000, "未找到页面"),
    BASE_ACTION_MODEL_IS_EMPTY_ERROR(ERROR_TYPE.BIZ_ERROR, 10021001, "未配置动作模型"),
    BASE_SYSTEM_CONFIG_IS_NOT_COMPLETED_ERROR(ERROR_TYPE.BIZ_ERROR, 10021002, "模块参数配置未完成"),
    BASE_VIEW_ACTION_CONFIG_MODEL_ERROR(ERROR_TYPE.BIZ_ERROR, 10021003, "动作配置错误"),
    BASE_CLIENT_ACTION_CONFIG_MODEL_ERROR(ERROR_TYPE.BIZ_ERROR, 10021004, "动作配置错误"),
    BASE_URL_ACTION_CONFIG_MODEL_ERROR(ERROR_TYPE.BIZ_ERROR, 10021005, "动作配置错误");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    BaseExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}

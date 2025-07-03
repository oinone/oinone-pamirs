package pro.shushi.pamirs.sso.api.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "SSO模块错误枚举")
public enum SsoExpEnumerate implements ExpBaseEnum {

    SSO_INVALID_CODE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041000, "无效 CODE"),
    AUTH_FIELD_PERMISSION_FIELD_NULL_ERROR(ERROR_TYPE.BIZ_ERROR, 10041001, "CODE 校验错误"),
    SSO_GET_ACCESSTOKEN_ERROR(ERROR_TYPE.BIZ_ERROR, 10041002, "获取调用凭证出错"),
    SSO_PASSWORD_PARSER_ERROR(ERROR_TYPE.BIZ_ERROR, 10041003, "密码解析错误"),
    SSO_GET_PASSWORD_PUBLIC_ERROR(ERROR_TYPE.BIZ_ERROR, 10041004, "获取加密公钥出错"),
    SSO_GET_CODE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041005, "获取CODE生成失败"),
    SSO_GET_REDIRECT_PAGE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041006, "跳转页面失败"),
    SSO_LOGIN_PASSWORD_ERROR(ERROR_TYPE.BIZ_ERROR, 10041007, "账户密码登录失败"),
    SSO_GENERATE_ACCESS_TOKEN_ERROR(ERROR_TYPE.BIZ_ERROR, 10041008, "生成ACCESSTOKEN失败"),
    SSO_REDIRECT_PAGE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041009, "重定向回页面失败"),
    SSO_REFRESH_TOKEN_ERROR(ERROR_TYPE.BIZ_ERROR, 10041010, "刷新ACCESSTOKEN 失败"),
    SSO_PAMIRS_SERIALIZE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041011, "设置COOKIE 序列化失败"),
    SSO_PAMIRS_SERIALIZE_URL_ERROR(ERROR_TYPE.BIZ_ERROR, 10041012, "序列化 URL 失败"),
    SSO_PAMIRS_CHECK_IF_ONLINE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041013, "检查是否登录"),
    SSO_PAMIRS_LOGOUT_ERROR(ERROR_TYPE.BIZ_ERROR, 10041014, "退出登录失败"),
    SSO_PAMIRS_SERVER_SET_CACHE_ERROR(ERROR_TYPE.BIZ_ERROR, 10041015, "SSO 设置服务器缓存失败"),
    SSO_PAMIRS_MAP_KEY_SECRET_ERROR(ERROR_TYPE.BIZ_ERROR, 10041016, "获取加密键值对失败"),
    SSO_PAMIRS_CREATE_CLIENT_ERROR(ERROR_TYPE.BIZ_ERROR, 10041017, "SSO 创建应用失败"),
    SSO_PAMIRS_LOGIN_CLIENT_ID_ERROR(ERROR_TYPE.BIZ_ERROR, 10041018, "登录服务端失败"),
    SSO_PAMIRS_CLIENT_NOT_FONT_ERROR(ERROR_TYPE.BIZ_ERROR, 10041019, "客户端不存在");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    SsoExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

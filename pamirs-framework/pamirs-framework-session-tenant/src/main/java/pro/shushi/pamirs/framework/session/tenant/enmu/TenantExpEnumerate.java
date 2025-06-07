package pro.shushi.pamirs.framework.session.tenant.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "多租户错误枚举")
public enum TenantExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10075000, "系统异常"), BASE_TENANT_TENANT_NULL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10075001, "租户不能为空");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    TenantExpEnumerate(ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    public ERROR_TYPE getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

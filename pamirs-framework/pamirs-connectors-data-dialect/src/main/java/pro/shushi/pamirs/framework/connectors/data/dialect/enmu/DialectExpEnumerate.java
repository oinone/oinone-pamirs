package pro.shushi.pamirs.framework.connectors.data.dialect.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统连接器方言错误枚举")
public enum DialectExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10011000, "系统异常"), BASE_DIALECT_VERSION_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10011001, "未配置方言组件注解");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    DialectExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

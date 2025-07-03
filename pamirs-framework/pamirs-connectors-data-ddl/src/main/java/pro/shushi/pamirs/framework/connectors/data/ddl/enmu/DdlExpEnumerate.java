package pro.shushi.pamirs.framework.connectors.data.ddl.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统连接器语法错误枚举")
public enum DdlExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10013000, "系统异常"), BASE_DDL_SYM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10013001, "DDL语法错误"), BASE_DDL_TABLE_NAME_LENGTH_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10013002, "表名长度超过限制"), BASE_DDL_COLUMN_NAME_LENGTH_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10013003, "列名长度超过限制");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    DdlExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

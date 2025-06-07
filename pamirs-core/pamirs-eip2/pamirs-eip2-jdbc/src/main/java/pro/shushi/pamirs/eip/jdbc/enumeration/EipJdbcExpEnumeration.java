package pro.shushi.pamirs.eip.jdbc.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 集成JDBC错误枚举
 *
 * @author Adamancy Zhang at 21:57 on 2025-02-26
 */
@Errors(displayName = "集成JDBC错误枚举")
public enum EipJdbcExpEnumeration implements ExpBaseEnum {

    EIP_DESIGNER_CHOOSE_DB_API(ERROR_TYPE.BIZ_ERROR, 10036000, "请选择数据库API"), EIP_DESIGNER_PRECALL_DB_API_ERROR(ERROR_TYPE.BIZ_ERROR, 10036001, "请传递参数:");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    EipJdbcExpEnumeration(ERROR_TYPE type, int code, String msg) {
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

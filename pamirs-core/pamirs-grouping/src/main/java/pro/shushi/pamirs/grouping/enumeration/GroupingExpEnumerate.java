package pro.shushi.pamirs.grouping.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 分组异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
@Errors(displayName = "分组模块错误枚举")
public enum GroupingExpEnumerate implements ExpBaseEnum {

    QUERY_RELATION_FIELD_IS_NOT_FOUND(ERROR_TYPE.BIZ_ERROR, 10067001, "关联关系查询字段找不到 {}"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10067000, "系统异常"),
    ;

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    GroupingExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

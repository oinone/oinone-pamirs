package pro.shushi.pamirs.framework.gateways.rsql.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统查询协议错误枚举")
public enum RsqlExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016000, "系统异常"),
    BASE_NO_SUPPORT_RELATION_RSQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016001, "不支持的关系型查询"),
    BASE_NO_MATCH_ARGUMENT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016002, "找不到与参数匹配的值"),
    BASE_ARGUMENTS_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016003, "传递的参数错误"),
    BASE_NO_MATCH_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016004, "没有匹配的字段"),
    BASE_NOT_AVAILABLE_BOOL_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016005, "该查询请求异常,查询条件无效"),
    BASE_NULL_BOOL_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016006, "该查询请求异常,查询条件不存在"),
    BASE_NO_SUPPORT_OPERATION_RSQL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016007, "不支持的rsql操作符"),
    BASE_NO_SUPPORT_JSON_SEARCH_INDEX_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016008, "不支持的JsonSearch索引标记"),
    BASE_NO_MATCH_COLUMN_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10016009, "没有匹配的列名称");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    RsqlExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

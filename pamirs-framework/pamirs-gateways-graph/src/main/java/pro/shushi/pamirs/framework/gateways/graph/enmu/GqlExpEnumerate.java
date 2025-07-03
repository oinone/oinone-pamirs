package pro.shushi.pamirs.framework.gateways.graph.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统数据协议错误枚举")
public enum GqlExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10017000, "系统异常"),
    BASE_GRAPHQL_EMPTY_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017001, "协议配置错误"),
    BASE_SDL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017002, "协议配置错误"),
    BASE_SDL_MERGE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017003, "协议配置错误"),
    BASE_NAME_CONFLICT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017004, "前端协议技术名称冲突"),
    BASE_REFERENCE_MODEL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017005, "未配置关联模型"),
    BASE_DICTIONARY_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017006, "未配数据字典"),
    BASE_REQUEST_VARIABLE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017007, "请求参数错误"),
    BASE_ENUM_VALUE_NULL_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017008, "枚举值不允许为空"),
    BASE_FUN_NOT_FOUND_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017009, "请确认函数是否存在"),
    BASE_GRAPHQL_FIELD_UNDEFINED_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017010, "请求字段定义已删除，或该字段未定义"),
    BASE_FUN_NAMESPACE_NOT_FOUND_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017011, "请确认函数模型是否存在"),
    BASE_GRAPHQL_MOCK_FIELD_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017012, "未能识别的虚拟字段"),
    BASE_GRAPHQL_MOCK_FIELD_MAPPING_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10017013, "虚拟字段映射失败");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    GqlExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

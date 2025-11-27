package pro.shushi.pamirs.ux.common.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 用户体验包错误枚举
 *
 * @author Adamancy Zhang at 21:02 on 2025-11-26
 */
@Errors(displayName = "用户体验包公共错误枚举")
public enum UxCommonExpEnumerate implements ExpBaseEnum {

    MODEL_NOT_FOUND(ERROR_TYPE.SYSTEM_ERROR, 10180001, "未找到模型字段元数据 model: {}"),
    MODEL_FIELD_NOT_FOUND(ERROR_TYPE.SYSTEM_ERROR, 10180002, "未找到模型字段元数据 model: {}, field: {}"),
    SORT_FIELD_NOT_FOUND(ERROR_TYPE.SYSTEM_ERROR, 10180003, "未找到排序字段元数据 model: {}, field: {}"),
    GROUPING_FIELD_NOT_FOUND(ERROR_TYPE.SYSTEM_ERROR, 10180004, "未找到分组字段元数据 model: {}, field: {}"),
    STATISTIC_API_NOT_FOUND(ERROR_TYPE.SYSTEM_ERROR, 10180005, "不支持的统计方式 statistic: {}"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10189999, "未处理的系统异常");

    private final ExpBaseEnum.ERROR_TYPE type;

    private final int code;

    private final String msg;

    UxCommonExpEnumerate(ExpBaseEnum.ERROR_TYPE type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public ExpBaseEnum.ERROR_TYPE type() {
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

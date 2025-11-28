package pro.shushi.pamirs.ux.quickfilling.enumeration;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 快速填报异常
 *
 * @author Gesi at 10:44 on 2025/9/1
 */
@Errors(displayName = "快速填报模块错误枚举")
public enum QuickFillingExpEnumerate implements ExpBaseEnum {

    SELECT_CONVERTER_ERROR(ERROR_TYPE.BIZ_ERROR, 10068001, "无法获取指定字段的类型转换函数 model: {}, field: {}, ttype: {}"),
    CONVERT_ERROR(ERROR_TYPE.BIZ_ERROR, 10068002, "数据不符合规则，请修改后继续"),
    FIELD_VALIDATE_REQUIRED_ERROR(ERROR_TYPE.BIZ_ERROR, 10068003, "必填"),
    NON_NUMBER_ERROR(ERROR_TYPE.BIZ_ERROR, 10068004, "无效的数字"),
    NON_BOOLEAN_ERROR(ERROR_TYPE.BIZ_ERROR, 10068005, "无效的布尔值"),
    NON_DATETIME_ERROR(ERROR_TYPE.BIZ_ERROR, 10068006, "无效的日期时间值"),
    NON_DATE_ERROR(ERROR_TYPE.BIZ_ERROR, 10068007, "无效的日期值"),
    NON_TIME_ERROR(ERROR_TYPE.BIZ_ERROR, 10068008, "无效的时间值"),
    NON_YEAR_ERROR(ERROR_TYPE.BIZ_ERROR, 10068009, "无效的年份"),
    NON_ENUM_ERROR(ERROR_TYPE.BIZ_ERROR, 10068010, "无效的枚举值"),
    NON_MAP_ERROR(ERROR_TYPE.BIZ_ERROR, 10068011, "无效的键值对"),
    LABEL_FIELDS_EMPTY_ERROR(ERROR_TYPE.BIZ_ERROR, 10068011, "没有匹配字段，请配置标题字段或搜索字段"),
    NON_MATCH_RELATION_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068012, "未查询到匹配数据"),
    MATCH_MANY_RELATION_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068013, "查询到多条匹配数据"),
    COUNTRY_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068014, "未查询到匹配的国家数据"),
    PROVINCE_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068015, "未查询到匹配的省/州数据"),
    CITY_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068016, "未查询到匹配的城市数据"),
    DISTRICT_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068017, "未查询到匹配的区/县数据"),
    STREET_DATA_ERROR(ERROR_TYPE.BIZ_ERROR, 10068018, "未查询到匹配的街道数据"),
    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10068000, "系统异常");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    QuickFillingExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

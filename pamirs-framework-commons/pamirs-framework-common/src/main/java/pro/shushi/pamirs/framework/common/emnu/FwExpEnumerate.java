package pro.shushi.pamirs.framework.common.emnu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统通用错误枚举")
public enum FwExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003000, "服务器正忙，请稍后再试"),
    BASE_LIFECYCLE_NOT_READY_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003001, "系统正在启动中，请稍后再试"),
    BASE_META_SIMULATE_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003002, "元数据配置错误"),
    BASE_MODEL_CONFIG_INIT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003003, "此模型已初始化，不能再使用静态化方式初始化"),
    BASE_ONLY_ONE_AUTO_INCREMENT_PK(ERROR_TYPE.SYSTEM_ERROR, 10003004, "主键配置错误"),
    BASE_META_STATIC_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003005, "配置错误，@Model.Static与@MetaSimulator不可同时使用"),
    BASE_FIELD_ENHANCE_CONVERTER_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003006, "字段定义错误"),
    BASE_SAME_TABLE_NAME_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10003007, "配置错误，非扩展继承表名不能相同"),
    BASE_CHECK_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10050009, "校验失败，数据错误");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    FwExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

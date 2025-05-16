package pro.shushi.pamirs.framework.connectors.data.sql.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统连接器脚本错误枚举")
public enum SqlExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10012000, "系统异常"),
    BASE_MODEL_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012001, "请设置模型编码或者模型类"),
    BASE_COLUMN_TYPE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012002, "数据列类型定义错误"),
    BASE_COLUMN_TYPE2_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012003, "数据列类型定义错误"),
    BASE_MODEL_MODEL_IS_NOT_EXISTS_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012004, "请设置模型编码"),
    BASE_LAMBDA_QUERY_WRAPPER_SELECT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012005, "请初始化模型配置"),
    BASE_QUERY_WRAPPER_SELECT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10012006, "请初始化模型配置");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    SqlExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

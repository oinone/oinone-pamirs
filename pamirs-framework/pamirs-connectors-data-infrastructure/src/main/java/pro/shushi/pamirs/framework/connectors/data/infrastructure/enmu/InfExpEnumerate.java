package pro.shushi.pamirs.framework.connectors.data.infrastructure.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统连接器方言错误枚举")
public enum InfExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10014000, "系统异常"),
    BASE_MODEL_CONFIG_IS_NOT_EXIST_DEPENDENCY_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10014001, "获取不到模型配置，请检查模块依赖是否正确"),
    BASE_FETCH_DATABASE_INSTANCE_INFO_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10014002, "获取数据库错误"),
    BASE_GET_DATABASES_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10014003, "获取数据库错误"),
    BASE_UPDATE_SCHEMA_STRUCTURE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10014004, "更新表结构错误"),
    BASE_DIALECT_SCRIPT_RUN_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10014005, "脚本执行错误");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    InfExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

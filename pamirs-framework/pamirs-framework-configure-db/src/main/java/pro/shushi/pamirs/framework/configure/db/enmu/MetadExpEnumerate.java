package pro.shushi.pamirs.framework.configure.db.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Errors(displayName = "系统元数据加载错误枚举")
public enum MetadExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10005000, "系统异常"), BASE_META_MAPPER_CONFIG_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10005001, "元数据配置错误");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    MetadExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

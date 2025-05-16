package pro.shushi.pamirs.boot.common.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Base
@Errors(displayName = "系统启动器错误枚举")
public enum BootExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10018000, "系统异常"),
    BASE_BOOT_MODULE_IN_JAR_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018001, "启动模块中包含jar包或者数据库中不存在的模块"),
    BASE_BOOT_META_DATA_COMPUTE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018002, "元数据计算错误"),
    BASE_BOOT_LOAD_META_FROM_ANNOTATION_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018003, "获取元数据错误，请查看上方ERROR级别的日志，并对元数据注解进行校正"),
    BASE_BOOT_MODULE_MISSING_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018004, "启动模块中包含不存在的模块"),
    BASE_BOOT_EXCLUSION_MODULE_CONFLICT_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018005, "启动模块互斥模块中包含已安装模块"),
    BASE_BOOT_RELOAD_MODULE_NOT_INSTALLED_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018006, "重启模块中包含未安装模块"),
    BASE_BOOT_RELOAD_MODULE_NOT_INSTALLED2_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018007, "重启模块中包含未安装模块"),
    BASE_BOOT_META_ONLINE_OWNSIGN_CHECK_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10018008, "元数据在线模式下（-PmetaOnline=NEVER），必须配置ownSign");

    private ERROR_TYPE type;

    private int code;

    private String msg;

    BootExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

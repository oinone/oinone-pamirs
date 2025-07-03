package pro.shushi.pamirs.boot.standard.enmu;

import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

@Base
@Errors(displayName = "系统标准启动器错误枚举")
public enum BootStandardExpEnumerate implements ExpBaseEnum {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR, 10020000, "系统异常"),
    BASE_UPDATE_MODULE_ING_STATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020001, "修改模块开始状态失败"),
    BASE_UPDATE_MODULE_COMPLETED_STATE_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020002, "修改模块完成状态失败"),
    BASE_UPGRADE_META_DATA_IS_NOT_EXIST_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020003, "升级删除元数据失败，元数据不存在"),
    BASE_LOAD_MODEL_META_ACTION_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020004, "加载行为的元数据失败，系统异常"),
    BASE_LOAD_MODEL_META_MODEL_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020005, "加载模型的元数据失败，系统异常"),
    BASE_LOAD_MODULE_META_MENU_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020006, "加载模块的菜单元数据失败，系统异常"),
    BASE_LOAD_MODULE_META_MODULE_DATA_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020007, "加载模块的模块元数据失败，系统异常"),
    BASE_LOAD_MODULE_META_NO_MODULE_NAME_ERROR(ERROR_TYPE.SYSTEM_ERROR, 10020008, "加载模块失败，没有选择要加载的模块");

    private final ERROR_TYPE type;

    private final int code;

    private final String msg;

    BootStandardExpEnumerate(ERROR_TYPE type, int code, String msg) {
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

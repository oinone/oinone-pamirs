package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.ErrorType", displayName = "错误类型")
public enum ErrorTypeEnum implements IEnum<String> {

    SYSTEM_ERROR(ExpBaseEnum.ERROR_TYPE.SYSTEM_ERROR.getType(), "系统错误", "系统错误"),
    GENERIC_ERROR(ExpBaseEnum.ERROR_TYPE.GENERIC_ERROR.getType(), "普通错误", "普通错误"),
    REMOTE_ERROR(ExpBaseEnum.ERROR_TYPE.REMOTE_ERROR.getType(), "远程错误", "远程错误"),
    BIZ_ERROR(ExpBaseEnum.ERROR_TYPE.BIZ_ERROR.getType(), "业务错误", "业务错误"),
    SECURITY_ERROR(ExpBaseEnum.ERROR_TYPE.SECURITY_ERROR.getType(), "权限错误", "权限错误"),
    DATA_ERROR(ExpBaseEnum.ERROR_TYPE.DATA_ERROR.getType(), "数据错误", "数据错误"),
    LOGIC_ERROR(ExpBaseEnum.ERROR_TYPE.LOGIC_ERROR.getType(), "逻辑错误", "逻辑错误");

    private final String value;
    private final String displayName;
    private final String help;

    ErrorTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }

}
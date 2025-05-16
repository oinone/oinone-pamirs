package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 二进制传输类型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.BinaryTransferType", displayName = "二进制传输类型")
public enum BinaryTransferTypeEnum implements IEnum<String> {

    BYTE_ARRAY("BYTE_ARRAY", "二进制数组", "二进制数组"),
    STRING("STRING", "字符串", "字符串"),
    ;

    private final String value;

    private final String displayName;

    private final String help;

    BinaryTransferTypeEnum(String value, String displayName, String help) {
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

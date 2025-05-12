package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

@Dict(dictionary = ClientTypeEnum.DICTIONARY, displayName = "客户端类型枚举", summary = "客户端类型枚举")
public enum ClientTypeEnum implements BitEnum {

    PC(1L, "PC端", "PC端"),
    MOBILE(1L << 1, "移动端", "移动端"),
    PAD(1L << 2, "PAD端", "PAD端"),
    ;

    public static final String DICTIONARY = "base.ClientTypeEnum";

    private final Long value;
    private final String displayName;
    private final String help;

    ClientTypeEnum(Long value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public Long value() {
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

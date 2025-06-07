package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = AddressSourceTypeEnum.dictionary, displayName = "地址源类型枚举")
public enum AddressSourceTypeEnum implements IEnum<String> {

    HOME("HOME", "家庭", "家庭"),
    WORK("WORK", "工作", "工作"),
    CUSTOM("CUSTOM", "自定义", "自定义");

    public static final String dictionary = "resource.AddressSourceTypeEnum";

    private String value;
    private String displayName;
    private String help;

    AddressSourceTypeEnum(String value, String displayName, String help) {
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

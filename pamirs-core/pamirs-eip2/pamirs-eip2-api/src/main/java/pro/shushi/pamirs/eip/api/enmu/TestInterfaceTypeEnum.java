package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = TestInterfaceTypeEnum.dictionary, displayName = "测试接口类型")
public enum TestInterfaceTypeEnum implements IEnum<String> {

    SINGLE("SINGLE", "单接口", "单接口"),
    COMPONENT("COMPONENT", "组合接口", "组合接口");

    public static final String dictionary = "pamirs.eip.TestInterfaceTypeEnum";

    private String value;

    private String displayName;

    private String help;

    TestInterfaceTypeEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }
}
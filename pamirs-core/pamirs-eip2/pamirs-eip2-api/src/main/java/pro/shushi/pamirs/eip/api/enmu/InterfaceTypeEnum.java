package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = InterfaceTypeEnum.dictionary, displayName = "接口类型")
public enum InterfaceTypeEnum implements IEnum<String> {

    /**
     * 集成接口
     */
    INTEGRATION("integration", "集成接口", "集成接口"),

    /**
     * 开放接口
     */
    OPEN("open", "开放接口", "开放接口"),

    /**
     * 路由定义
     */
    ROUTE("route", "路由定义", "路由定义");

    public static final String dictionary = "pamirs.eip.InterfaceTypeEnum";

    private String value;

    private String displayName;

    private String help;

    InterfaceTypeEnum(String value, String displayName, String help) {
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

    public static InterfaceTypeEnum safeValueOf(String value) {
        for (InterfaceTypeEnum item : InterfaceTypeEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}

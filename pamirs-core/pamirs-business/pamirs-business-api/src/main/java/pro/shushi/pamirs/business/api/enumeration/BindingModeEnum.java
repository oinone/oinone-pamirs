package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(
        dictionary = BindingModeEnum.dictionary,
        displayName = "用户绑定方式"
)
public enum BindingModeEnum implements IEnum<String> {


    CREATE_BINDING("CREATE_BINDING", "创建并绑定用户", "创建并绑定用户"),

    BINDING_EXISTING("BINDING_EXISTING", "绑定已有用户", "绑定已有用户");


    public static final String dictionary = "pamirs.business.BindingModeEnum";

    private String value;

    private String displayName;

    private String help;

    private BindingModeEnum(String value, String displayName, String help) {
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

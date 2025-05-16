package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ContextTypeEnum.dictionary, displayName = "上下文类型", summary = "用于指定接口参数转换时上下文的来源")
public enum ContextTypeEnum implements IEnum<String> {

    EXECUTOR("EXECUTOR", "执行器上下文", "执行器上下文"),
    INTERFACE("INTERFACE", "接口上下文", "接口上下文");

    public static final String dictionary = "pamirs.eip.ContextTypeEnum";

    private String value;

    private String displayName;

    private String help;

    ContextTypeEnum(String value, String displayName, String help) {
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

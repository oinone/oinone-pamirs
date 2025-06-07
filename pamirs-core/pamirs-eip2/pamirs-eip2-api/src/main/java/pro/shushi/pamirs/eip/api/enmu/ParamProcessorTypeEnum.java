package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = ParamProcessorTypeEnum.dictionary, displayName = "参数处理器类型")
public enum ParamProcessorTypeEnum implements IEnum<String> {

    REQUEST("REQUEST", "请求", "请求"),
    RESPONSE("RESPONSE", "响应", "响应");

    public static final String dictionary = "pamirs.eip.EipParamProcessorTypeEnum";

    private String value;

    private String displayName;

    private String help;

    ParamProcessorTypeEnum(String value, String displayName, String help) {
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

package pro.shushi.pamirs.eip.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = HttpParamTypeEnum.dictionary, displayName = "http请求参数类型", summary = "用于指定参数转换时的处理方式")
public enum HttpParamTypeEnum implements IEnum<String> {

    PATH("PATH", "请求路径", "请求路径"),
    HEADER("HEADER", "请求头", "请求头"),
    BODY("BODY", "请求体", "请求体"),
    METHOD("METHOD", "请求方法", "请求方法"),
    ;

    public static final String dictionary = "pamirs.eip.HttpParamTypeEnum";

    private String value;

    private String displayName;

    private String help;

    HttpParamTypeEnum(String value, String displayName, String help) {
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
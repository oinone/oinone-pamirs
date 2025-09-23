package pro.shushi.pamirs.eip.designer.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnAuthType
 *
 * @author yakir on 2023/03/29 15:25.
 */
@Base
@Dict(dictionary = OpenAuthType.dictionary, displayName = "开放接口认证方式", summary = "开放接口认证方式")
public enum OpenAuthType implements IEnum<String> {

    NONE("NONE", "无访问认证", "无访问认证"),
    OCA("OCA", "非加密访问认证", "非加密访问认证"),
    MD5("MD5", "MD5加密访问认证", "MD5加密访问认证"),
    CUSTOM("CUSTOM", "自定义认证", "自定义认证"),
    ;

    public static final String dictionary = "designer.OpenAuthType";

    private final String value;
    private final String displayName;
    private final String help;

    OpenAuthType(String value, String displayName, String help) {
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

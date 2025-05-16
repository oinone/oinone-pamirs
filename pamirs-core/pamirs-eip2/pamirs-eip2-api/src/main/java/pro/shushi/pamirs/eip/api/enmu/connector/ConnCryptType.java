package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnCryptType
 *
 * @author yakir on 2023/04/23 10:19.
 */
@Base
@Dict(dictionary = ConnCryptType.dictionary, displayName = "加密方式", summary = "加密方式")
public enum ConnCryptType implements IEnum<String> {

    NO_CRYPT("NO_CRYPT", "无需加密", "无需加密"),
    BASE64("BASE64", "BASE64", "BASE64"),
    BASE128("BASE128", "BASE128", "BASE128"),
    MD5("MD5", "MD5", "MD5"),
    SHA("SHA", "SHA", "SHA"),
    SHA256("SHA256", "SHA256", "SHA256"),
    SHA384("SHA384", "SHA384", "SHA384"),
    SHA512("SHA512", "SHA512", "SHA512"),

    ;

    public static final String dictionary = "designer.ConnCryptType";

    private final String value;
    private final String displayName;
    private final String help;

    ConnCryptType(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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

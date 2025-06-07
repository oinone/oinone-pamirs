package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "bicenum", displayName = "")
public enum BICEnum implements IEnum<String> {

    BIC("BIC", "BIC Code", "BIC Code"),
    SWIFT("SWIFT", "SWIFT Code", "SWIFT Code");

    private String help;

    private String value;

    private String displayName;

    BICEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
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

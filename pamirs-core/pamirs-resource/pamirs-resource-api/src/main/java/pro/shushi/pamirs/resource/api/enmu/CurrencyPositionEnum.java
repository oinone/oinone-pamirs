package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "currencypositionenum", displayName = "")
public enum CurrencyPositionEnum implements IEnum<String> {

    BEFORE("BEFORE", "货币符号在前", "货币符号在前"),
    AFTER("AFTER", "货币符号在后", "货币符号在后");

    private String help;

    private String value;

    private String displayName;

    CurrencyPositionEnum(String value, String displayName, String help) {
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

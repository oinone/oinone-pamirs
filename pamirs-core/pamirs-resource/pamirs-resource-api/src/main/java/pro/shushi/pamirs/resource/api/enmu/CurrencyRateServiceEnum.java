package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = CurrencyRateServiceEnum.DICTIONARY)
public enum CurrencyRateServiceEnum implements IEnum<String> {

    DEFAULT_SERVICE("DEFAULT_SERVICE", "聚合数据", "聚合数据"),
    ;

    public static final String DICTIONARY = "currencyrateserviceenum";

    private final String value;
    private final String displayName;
    private final String help;

    CurrencyRateServiceEnum(String value, String displayName, String help) {
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

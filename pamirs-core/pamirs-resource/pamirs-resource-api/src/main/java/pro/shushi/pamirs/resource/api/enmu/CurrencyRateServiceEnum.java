package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "currencyrateserviceenum", displayName = "")
public enum CurrencyRateServiceEnum implements IEnum<String> {
    DEFAULT_SERVICE("聚合数据", "聚合数据", "聚合数据"),
    ;

    private String help;

    private String value;

    private String displayName;

    CurrencyRateServiceEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}

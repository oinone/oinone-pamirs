package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "vatLabelEnum", displayName = "")
public enum VatLabelEnum implements IEnum<String> {

    VAT("VAT", "增值税", "增值税"),
    GST("GST", "GST", "GST"),
    HST("HST", "HST", "HST"),
    RFC("RFC", "RFC", "RFC"),
    RNC("RNC", "RNC", "RNC"),
    NIT("NIT", "NIT", "NIT"),
    ;

    private String help;

    private String value;

    private String displayName;

    VatLabelEnum(String value, String displayName, String help) {
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

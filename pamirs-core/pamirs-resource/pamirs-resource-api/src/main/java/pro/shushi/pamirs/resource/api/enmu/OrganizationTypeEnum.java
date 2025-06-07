package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = OrganizationTypeEnum.dictionary, displayName = "组织类型")
public enum OrganizationTypeEnum implements IEnum<String> {

    COMPANY("COMPANY", "公司", "公司"),
    INDIVIDUAL_BUSINESS("INDIVIDUAL_BUSINESS", "个体工商户", "个体工商户"),
    PUBLIC_WELFARE("PUBLIC_WELFARE", "公益组织", "公益组织"),
    PERSON("PERSON", "个人", "个人");

    public static final String dictionary = "resource.OrganizationTypeEnum";

    private String value;
    private String displayName;
    private String help;

    OrganizationTypeEnum(String value, String displayName, String help) {
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

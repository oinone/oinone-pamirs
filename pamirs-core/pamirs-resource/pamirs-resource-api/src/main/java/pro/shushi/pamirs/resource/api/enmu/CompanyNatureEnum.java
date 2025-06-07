package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = CompanyNatureEnum.dictionary, displayName = "公司性质")
public enum CompanyNatureEnum implements IEnum<String> {

    SELF_EMPLOYEE("SELF_EMPLOYEE", "个体户", "个体户"),
    JOINT_VENTURES("JOINT_VENTURES", "合资", "合资"),
    SOLE_PROPRIETORSHIP("SOLE_PROPRIETORSHIP", "独资", "独资"),
    STATE_OWNED("STATE_OWNED", "国有", "国有"),
    PRIVATE("PRIVATE", "私营", "私营"),
    OWNERSHIP_BY_THE_WHOLE_PEOPLE("OWNERSHIP_BY_THE_WHOLE_PEOPLE", "全民所有制", "全民所有制"),
    COLLECTIVE_OWNERSHIP("COLLECTIVE_OWNERSHIP", "集体所有制", "集体所有制"),
    STOCK_CORPORATION("STOCK_CORPORATION", "股份有限公司", "股份有限公司"),
    LTD("LTD", "有限责任公司", "有限责任公司"),
    ;

    public static final String dictionary = "resource.CompanyTypeEnum";

    private String help;

    private String value;

    private String displayName;

    CompanyNatureEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
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

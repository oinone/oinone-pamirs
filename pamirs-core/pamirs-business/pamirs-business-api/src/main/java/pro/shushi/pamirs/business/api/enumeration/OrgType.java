package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * OrgType
 *
 * @author yakir on 2022/09/13 15:33.
 */
@Dict(dictionary = OrgType.dict, displayName = "其他-组织类型")
public enum OrgType implements IEnum<String> {

    ORG_GONG_YI("ORG_GONG_YI", "公益机构", "公益机构"),
    ORG_HANG_YE_XIE_HUI("ORG_HANG_YE_XIE_HUI", "行业协会", "行业协会"),
    ORG_ZONG_JIAO("ORG_ZONG_JIAO", "宗教组织", "宗教组织"),
    ORG_XUE_SHEN("ORG_XUE_SHEN", "学生组织", "学生组织"),
    ORG_GONG_QING_TUAN("ORG_GONG_QING_TUAN", "共青团", "共青团"),
    ORG_GUO_JI("ORG_GUO_JI", "国际机构", "国际机构"),
    ORG_FEI_YING_LI("ORG_FEI_YING_LI", "非盈利机构", "非盈利机构"),
    ORG_GONG_HUI("ORG_GONG_HUI", "工会", "工会"),
    ORG_JI_JIN_HUI("ORG_JI_JIN_HUI", "基金会", "基金会"),
    ORG_WEI_HUI("ORG_WEI_HUI", "村委会/居委会", "村委会/居委会"),
    ORG_OTHERS("ORG_OTHERS", "其他社会组织", "其他社会组织"),

    DEFAULT_TYPE("DEFAULT_TYPE", "PAMIRS", "PAMIRS"),

    ;

    public static final String dict = "business.OrgType";

    private final String value;
    private final String displayName;
    private final String help;

    OrgType(String value, String displayName, String help) {
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

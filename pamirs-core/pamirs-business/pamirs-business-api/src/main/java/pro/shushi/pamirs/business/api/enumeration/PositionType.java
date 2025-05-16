package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * PositionType
 *
 * @author yakir on 2022/09/13 15:45.
 */
@Dict(dictionary = PositionType.dict, displayName = "职务类型")
public enum PositionType implements IEnum<String> {

    POS_CEO("POS_CEO", "法人/CEO/老板", "法人/CEO/老板"),
    POS_REN_SHI("POS_REN_SHI", "人事主管", "人事主管"),
    POS_CAI_WU("POS_CAI_WU", "财务主管", "财务主管"),
    POS_XIAO_SHOU("POS_XIAO_SHOU", "销售主管", "销售主管"),
    POS_XING_ZHENG("POS_XING_ZHENG", "行政主管", "行政主管"),
    POS_IT("POS_IT", "IT主管", "IT主管"),
    POS_SHIC_HANG("POS_SHIC_HANG", "市场主管", "市场主管"),
    POS_YUN_YING("POS_YUN_YING", "运营主管", "运营主管"),
    POS_PU_TONG("POS_PU_TONG", "普通员工", "普通员工"),
    POS_OTHERS("POS_OTHERS", "其他", "其他"),

    DEFAULT_TYPE("DEFAULT_TYPE", "PAMIRS", "PAMIRS"),

    ;

    public static final String dict = "business.PositionType";


    private final String value;
    private final String displayName;
    private final String help;

    PositionType(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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

package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * GovType
 *
 * @author yakir on 2022/09/13 16:49.
 */
@Dict(dictionary = GovType.dict, displayName = "政府-单位类型")
public enum GovType implements IEnum<String> {

    GOV_DANG_QUN("GOV_DANG_QUN", "党群", "党群", 0, null),
    GOV_DANG_WEI("GOV_DANG_WEI", "党委", "党委", 1, GOV_DANG_QUN),
    GOV_JI_WEI("GOV_JI_WEI", "纪委", "纪委", 1, GOV_DANG_QUN),
    GOV_TUAN_WEI("GOV_TUAN_WEI", "团委", "团委", 1, GOV_DANG_QUN),

    GOV_MIN_SHENG("GOV_MIN_SHENG", "民生", "民生", 0, null),
    GOV_MIN_ZHENG("GOV_MIN_ZHENG", "民政", "民政", 1, GOV_MIN_SHENG),
    GOV_WEN_HUA("GOV_WEN_HUA", "文化", "文化", 1, GOV_MIN_SHENG),
    GOV_GUANG_DIAN("GOV_GUANG_DIAN", "广电", "广电", 1, GOV_MIN_SHENG),
    GOV_REN_SHE("GOV_REN_SHE", "人社", "人社", 1, GOV_MIN_SHENG),
    GOV_JIAO_TONG("GOV_JIAO_TONG", "交通", "交通", 1, GOV_MIN_SHENG),
    GOV_WEI_SHENG("GOV_WEI_SHENG", "卫生", "卫生", 1, GOV_MIN_SHENG),
    GOV_YI_BAO("GOV_YI_BAO", "医保", "医保", 1, GOV_MIN_SHENG),

    GOV_ZHENG_FA("GOV_ZHENG_FA", "政法", "政法", 0, null),
    GOV_GONG_AN("GOV_GONG_AN", "公安", "公安", 1, GOV_ZHENG_FA),
    GOV_JIAN_CHA_YUAN("GOV_JIAN_CHA_YUAN", "检察院", "检察院", 1, GOV_ZHENG_FA),
    GOV_FA_YUAN("GOV_FA_YUAN", "法院", "法院", 1, GOV_ZHENG_FA),
    GOV_SI_FA("GOV_SI_FA", "司法", "司法", 1, GOV_ZHENG_FA),

    GOV_JING_FA("GOV_JING_FA", "经发", "经发", 0, null),
    GOV_FA_GAI_WEI("GOV_FA_GAI_WEI", "发改委", "发改委", 1, GOV_JING_FA),
    GOV_JING_XIN_WEI("GOV_JING_XIN_WEI", "经信委", "经信委", 1, GOV_JING_FA),
    GOV_SHANG_WU_JU("GOV_SHANG_WU_JU", "商务局", "商务局", 1, GOV_JING_FA),
    GOV_TONG_JI_JU("GOV_TONG_JI_JU", "统计局", "统计局", 1, GOV_JING_FA),

    GOV_CHENG_JIAN("GOV_CHENG_JIAN", "城建", "城建", 0, null),
    GOV_GUI_HUA("GOV_GUI_HUA", "规划", "规划", 1, GOV_CHENG_JIAN),
    GOV_GUO_TU("GOV_GUO_TU", "国土", "国土", 1, GOV_CHENG_JIAN),
    GOV_JIAN_SHE("GOV_JIAN_SHE", "建设", "建设", 1, GOV_CHENG_JIAN),

    GOV_JIAN_GUAN("GOV_JIAN_GUAN", "监管", "监管", 0, null),
    GOV_SHUI_WU("GOV_SHUI_WU", "税务", "税务", 1, GOV_JIAN_GUAN),
    GOV_HAI_GUAN("GOV_HAI_GUAN", "海关", "海关", 1, GOV_JIAN_GUAN),
    GOV_GONG_SHANG("GOV_GONG_SHANG", "工商", "工商", 1, GOV_JIAN_GUAN),
    GOV_HUAN_BAO("GOV_HUAN_BAO", "环保", "环保", 1, GOV_JIAN_GUAN),
    GOV_WU_JIA("GOV_WU_JIA", "物价", "物价", 1, GOV_JIAN_GUAN),
    GOV_YAO_PIN("GOV_YAO_PIN", "药品", "药品", 1, GOV_JIAN_GUAN),

    GOV_SHI_WEI("GOV_SHI_WEI", "市委", "市委", 0, null),
    GOV_SHI_ZHENG_FU("GOV_SHI_ZHENG_FU", "市政府", "市政府", 1, GOV_SHI_WEI),

    GOV_XIAN_WEI("GOV_XIAN_WEI", "县委", "县委", 0, null),
    GOV_XIAN_ZHENG_FU("GOV_XIAN_ZHENG_FU", "县政府", "县政府", 1, GOV_XIAN_WEI),

    GOV_SHENG_WEI("GOV_SHENG_WEI", "省委", "省委", 0, null),
    GOV_SHENG_ZHENG_FU("GOV_SHENG_ZHENG_FU", "省政府", "省政府", 1, GOV_SHENG_WEI),

    GOV_ZHEN_JI_ZHENG_FU("GOV_ZHEN_JI_ZHENG_FU", "镇级政府", "镇级政府", 0, null),

    GOV_GONG_GONG_SHE_SHI_GUAN_LI("GOV_GONG_GONG_SHE_SHI_GUAN_LI", "公共设施管理", "公共设施管理", 0, null),

    GOV_JI_CENG_ZHI_LI("GOV_JI_CENG_ZHI_LI", "基层治理", "基层治理", 0, null),

    GOV_QI_TA_ZHENG_FU_BU_MEN("GOV_QI_TA_ZHENG_FU_BU_MEN", "其他政府部门", "其他政府部门", 0, null),

    GOV_QI_TA_SHI_YE_DAN_WEI("GOV_QI_TA_SHI_YE_DAN_WEI", "其他事业单位", "其他事业单位", 0, null),

    ;


    public static final String dict = "business.GovType";

    private final String value;
    private final String displayName;
    private final String help;
    private final Integer depth;
    private final GovType parent;


    GovType(String value, String displayName, String help, Integer depth, GovType parent) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.depth = depth;
        this.parent = parent;
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

    public Integer getDepth() {
        return depth;
    }

    public GovType getParent() {
        return parent;
    }
}

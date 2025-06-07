package pro.shushi.pamirs.apps.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author shier
 * date  2021/5/25 10:09 上午
 */
@Base
@Dict(dictionary = ModuleCategoryType.dictionary, displayName = "模块分类类型")
public enum ModuleCategoryType implements IEnum<String> {
    BUSINESS_GROWTH("BUSINESS_GROWTH", "促进业务快速增长", null, 1),
    ELECTRONIC_COMMERCE("ELECTRONIC_COMMERCE", "电商", BUSINESS_GROWTH.value, 101),
    DISTRIBUTION("DISTRIBUTION", "分销", BUSINESS_GROWTH.value, 102),
    OPERATOR("OPERATOR", "运营", BUSINESS_GROWTH.value, 103, Boolean.FALSE),
    MARKETING("MARKETING", "全员营销", BUSINESS_GROWTH.value, 104),
    DESIGN("DESIGN", "设计", BUSINESS_GROWTH.value, 105),
    OPERATOR_BASE("OPERATOR_BASE", "运营基础", BUSINESS_GROWTH.value, 106, Boolean.FALSE),

    LOW_CODE_DESIGN("LOW_CODE_DESIGN", "低代码设计", null, 2),
    USER_EXPERIENCE("USER_EXPERIENCE", "用户体验", LOW_CODE_DESIGN.value, 201),
    BUSINESS_PROCESS("BUSINESS_PROCESS", "业务流程", LOW_CODE_DESIGN.value, 202),
    DESIGNER_COMMON("DESIGNER_COMMON", "公共", LOW_CODE_DESIGN.value, 202, Boolean.FALSE),

    INDUSTRY_GENERAL_MODEL("INDUSTRY_GENERAL_MODEL", "成熟的行业通用数据模型（CDM）", null, 3),
    TRANSACTION("TRANSACTION", "交易", INDUSTRY_GENERAL_MODEL.value, 301),
    SETTLEMENT("SETTLEMENT", "结算", INDUSTRY_GENERAL_MODEL.value, 302),

    OPERATION_SUPPORT("OPERATION_SUPPORT", "高效的运营支撑", null, 4),
    PROCESS_AUTOMATION("PROCESS_AUTOMATION", "流程自动化", OPERATION_SUPPORT.value, 401),
    BUSINESS_ANALYSIS("BUSINESS_ANALYSIS", "业务分析", OPERATION_SUPPORT.value, 402),

    BASE_SUPPORT("BASE_SUPPORT", "稳定的基础支持", null, 5),
    PARTNER("PARTNER", "合作伙伴", BASE_SUPPORT.value, 501),
    DATA_PLATFORM("DATA_PLATFORM", "数据", BASE_SUPPORT.value, 502),
    OPEN_PLATFORM("OPEN_PLATFORM", "集成", BASE_SUPPORT.value, 503),
    TRANSLATE("TRANSLATE", "国际化", BASE_SUPPORT.value, 504),
    USER_AND_AUTH("USER_AND_AUTH", "用户与权限", BASE_SUPPORT.value, 505),
    APPS_AND_CMP("APPS_AND_CMP", "应用与多云管理", BASE_SUPPORT.value, 506),
    RESOURCE("RESOURCE", "资源", BASE_SUPPORT.value, 507),
    BASE_CAPABILITY("BASE_CAPABILITY", "基础能力", BASE_SUPPORT.value, 508),
    ;

    public static final String dictionary = "app.ModuleCategoryType";

    private String value;
    private String displayName;
    private String parentCode;
    private Integer sequence;
    private Boolean screenVisible;

    ModuleCategoryType(String value, String displayName, String parentCode, Integer sequence) {
        this(value, displayName, parentCode, sequence, Boolean.TRUE);
    }

    ModuleCategoryType(String value, String displayName, String parentCode, Integer sequence, Boolean screenVisible) {
        this.value = value;
        this.displayName = displayName;
        this.parentCode = parentCode;
        this.sequence = sequence;
        this.screenVisible = screenVisible;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Boolean getScreenVisible() {
        return screenVisible;
    }
}
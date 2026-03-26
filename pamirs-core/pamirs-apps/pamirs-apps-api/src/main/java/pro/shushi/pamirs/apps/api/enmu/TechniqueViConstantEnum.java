package pro.shushi.pamirs.apps.api.enmu;

import pro.shushi.pamirs.apps.AppsModule;
import pro.shushi.pamirs.core.common.CommonI18nUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * @author shier
 * date  2021/5/25 10:09 上午
 */
@Base
@Dict(dictionary = TechniqueViConstantEnum.DICTIONARY, displayName = "平台技术架构图常量")
public enum TechniqueViConstantEnum implements IEnum<Integer> {

    WORKFLOW(1, "工作流", ""),
    MESSAGE_QUEUE(2, "消息队列", ""),
    TRIGGER(3, "触发器", ""),
    EXTEND_POINT(4, "扩展点", ""),
    INTERCEPTOR(5, "拦截器", ""),
    DATA_CHANGE(6, "数据转换", ""),
    DATA_SYNC(7, "数据同步", ""),
    EIP_INTERFACE(8, "集成接口", ""),
    PRODUCT(9, "产品", ""),
    LOGIC(10, "逻辑", ""),
    BUSINESS_COMPONENT(11, "业务组件", ""),
    ROUTER_RULE(12, "路由规则", ""),
    VALIDATION_RULE(13, "校验规则", ""),
    DATA_FILTER_RULE(14, "数据过滤规则", ""),
    MODEL(15, "模型", ""),
    PROFESSION_MODEL(16, "行业模型", ""),
    BUSINESS_MODEL(17, "业务模型", ""),
    BASE_MODEL(18, "基础模型", ""),
    USER_EXPERIENCE(19, "用户体验", ""),
    FORM(20, "表单", ""),
    LAYOUT(21, "布局", ""),
    THEME(22, "主题", ""),
    INTERACTIVE_COMPONENT(23, "交互组件", ""),
    TEMPLATE(24, "模板", ""),
    LINK(25, "链接", ""),
    VIEW(26, "视图", ""),
    DATA(27, "数据", ""),
    CHART(28, "图表", ""),
    REPORT(29, "报表", ""),
    SCREEN(30, "大屏", ""),
    STORAGE(31, "对象存储", ""),
    CACHE(32, "缓存", ""),
    DATA_SOURCE(33, "数据源", ""),
    SEARCH(34, "搜索", ""),
    DISTRIBUTE_CONFIG(35, "分布式配置", ""),
    MICRO_SERVICE(36, "微服务", ""),
    BUSINESS(37, "业务域", ""),
    TERMINAL(38, "客户端", ""),
    LINE(39, "产品线", ""),
    APPLICATION(40, "应用", "");

    public static final String DICTIONARY = "app.TechniqueViConstantEnum";

    private final int value;
    private final String displayName;
    private final String help;

    TechniqueViConstantEnum(int value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTranslate() {
        return CommonI18nUtils.translateDataDictionaryItem(AppsModule.MODULE_MODULE, DICTIONARY, this);
    }

    public String getHelp() {
        return help;
    }
}
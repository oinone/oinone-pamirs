package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 视图类型枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = ViewTypeEnum.DICTIONARY, displayName = "视图类型")
public enum ViewTypeEnum implements IEnum<String> {

    CUSTOM("CUSTOM", "自定义视图类型", "自定义视图类型"),

    DETAIL("DETAIL", "详情", "详情"),
    FORM("FORM", "表单", "表单"),
    TABLE("TABLE", "表格", "表格"),
    SEARCH("SEARCH", "搜索", "搜索"),
    CALENDAR("CALENDAR", "日历", "日历"),
    KANBAN("KANBAN", "看板", "看板"),
    GALLERY("GALLERY", "画廊", "画廊"),
    CHART("CHART", "图表", "图表"),
    TREE("TREE", "树", "树"),
    ;

    public static final String DICTIONARY = "base.ViewType";

    private final String value;

    private final String displayName;

    private final String help;

    ViewTypeEnum(String value, String displayName, String help) {
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
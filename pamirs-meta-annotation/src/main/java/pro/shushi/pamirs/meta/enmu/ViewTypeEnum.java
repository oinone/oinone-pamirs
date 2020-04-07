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
@Dict(dictionary = "base.ViewType", displayName = "视图类型")
public enum ViewTypeEnum implements IEnum<String> {

    DETAIL("DETAIL", "详情", "详情"),
    FORM("FORM", "表单", "表单"),
    TABLE("TABLE", "表格", "表格"),
    CALENDAR("CALENDAR", "日历", "日历"),
    KANBAN("KANBAN", "看板", "看板"),
    GALLERY("GALLERY", "画廊",  "画廊"),
    CUSTOM("CUSTOM", "自定义视图类型", "自定义视图类型"),
    CHART("CHART", "报表", "报表"),
    SEARCH("SEARCH", "搜索", "搜索"),
    ;

    private String value;

    private String displayName;

    private String help;

    ViewTypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}
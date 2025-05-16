package pro.shushi.pamirs.boot.base.ux.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 模板插槽枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.TemplateSlotName", displayName = "模板插槽枚举")
public enum ViewSlotNameEnum implements IEnum<String> {

    ACTIONS("actions", "视图级动作列表", "视图级动作列表"),
    FIELDS("fields", "视图级字段列表", "视图级字段列表"),
    ROW_ACTIONS("rowActions", "行内动作列表", "行内动作列表"),

    SEARCH("search", "搜索栏", "搜索栏"),
    SEARCH_FIELDS("searchFields", "搜索控件栏", "搜索控件栏"),
    SEARCH_ACTION_BAR("searchActionBar", "搜索按钮栏", "搜索按钮栏"),
    ACTION_BAR("actionBar", "按钮栏", "按钮栏"),
    TABLE_AREA("tableArea", "表格区", "表格区"),
    TABLE("table", "主表格", "主表格"),
    FORM("form", "主表单", "主表单"),
    FORM_FIELDS("formFields", "表单字段区", "表单字段区"),
    DETAIL("detail", "详情区", "详情区"),
    DETAIL_FIELDS("detailFields", "详情字段区", "详情字段区");

    private final String help;
    private final String value;
    private final String displayName;

    ViewSlotNameEnum(String value, String displayName, String help) {
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
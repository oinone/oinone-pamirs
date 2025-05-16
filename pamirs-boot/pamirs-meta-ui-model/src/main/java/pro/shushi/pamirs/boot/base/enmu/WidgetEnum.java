package pro.shushi.pamirs.boot.base.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 组件枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:53 下午
 */
@Base
@Dict(dictionary = "base.Widget", displayName = "组件")
public enum WidgetEnum implements IEnum<String> {

    SLOT("slot", "插槽", "插槽"),
    CONTAINER("container", "容器", "容器"),

    BLOCK("block", "区块", "区块"),
    GROUP("group", "分组", "分组"),
    TABS("tabs", "选项卡", "选项卡"),
    TAB("tab", "选项卡页", "选项卡页"),

    LOGO("logo", "LOGO", "LOGO"),
    APP_FINDER("appFinder", "应用选择", "应用选择"),
    NOTIFICATION("notification", "通知", "通知"),
    DIVIDER("divider", "分割线", "分割线"),
    DIVIDER_VERTICAL("divider_vertical", "垂直分割线", "垂直分割线"),
    LANGUAGE("language", "语言选择", "语言选择"),
    USER_PROFILE("userProfile", "用户", "用户"),
    NAV_MENU("navMenu", "导航", "导航"),
    BREADCRUMB("breadcrumb", "面包屑", "面包屑"),
    MAIN_VIEW("mainView", "主视图", "主视图"),

    READ_ONLY("Text", "只读", "只读"),

    INPUT("Input", "输入框", "输入框"),
    TEXT("TextArea", "多行文本", "多行文本"),
    RICH_TEXT("RichText", "富文本", "富文本"),
    EMAIL("Email", "邮箱", "邮箱"),
    PHONE("Phone", "手机", "手机"),
    PASSWORD("Password", "密码", "密码"),

    INTEGER("Integer", "整数", "整数"),
    FLOAT("Float", "小数", "小数"),
    CURRENCY("Currency", "金额", "金额"),
    PERCENT("Percent", "百分比", "百分比"),

    CHECK_BOX("CheckBox", "复选框", "复选框"),
    SWITCH("Switch", "开关", "开关"),

    SELECT("Select", "下拉选择", "下拉选择"),
    TABLE_SELECT("TableSelect", "表格选择", "表格选择"),
    RADIO("Radio", "单选框", "单选框"),

    DATE_PICKER("DatePicker", "日期选择器", "日期选择器"),
    DATE_TIME_PICKER("DateTimePicker", "日期时间选择器", "日期时间选择器"),
    TIME_PICKER("TimePicker", "时间选择器", "时间选择器"),

    RANGE_DATE_PICKER("RangeDatePicker", "日期范围选择器", "日期范围选择器"),
    RANGE_DATE_TIME_PICKER("RangeDateTimePicker", "日期时间范围选择器", "日期时间范围选择器"),
    RANGE_TIME_PICKER("RangeTimePicker", "时间范围选择器", "时间范围选择器"),

    UPLOAD("Upload", "文件上传", "文件上传"),
    UPLOAD_IMG("UploadImg", "图片上传", "图片上传"),

    BUTTON("button", "按钮", "按钮"),
    BUTTONS("buttons", "按钮集", "按钮集"),

    TREE("Tree", "树形控件", "树形控件"),
    FORM("Form", "表单", "表单"),
    TABLE("Table", "表格", "表格"),
    DETAIL("Detail", "详情", "详情"),

    ;

    private final String displayName;

    private final String value;

    private final String help;

    WidgetEnum(String value, String displayName, String help) {
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
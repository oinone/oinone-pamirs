package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.*;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceDateFormat;
import pro.shushi.pamirs.resource.api.tmodel.lang.ResourceTimeFormat;

import java.util.List;

@Model.model("resource.ResourceLang")
@Model.Advanced(name = "resourceLang", unique = {"code"})
@Model(displayName = "语言", labelFields = "name")
@UxRouteButton(
        action = @UxAction(name = "resourceLangCreate", label = "创建", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingView = "语言table", bindingType = ViewTypeEnum.TABLE),
        value = @UxRoute(model = ResourceLang.MODEL_MODEL, viewName = "语言form", viewType = ViewTypeEnum.FORM, openType = ActionTargetEnum.ROUTER)
)
@UxRouteButton(
        action = @UxAction(name = "resourceLangDetail", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = ResourceLang.MODEL_MODEL, viewName = "语言detail", viewType = ViewTypeEnum.DETAIL, openType = ActionTargetEnum.ROUTER)
)
@UxRouteButton(
        action = @UxAction(name = "resourceLangEdit", label = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = ResourceLang.MODEL_MODEL, viewName = "语言form", viewType = ViewTypeEnum.FORM, openType = ActionTargetEnum.ROUTER)
)
public class ResourceLang extends CodeModel {

    public static final String MODEL_MODEL = "resource.ResourceLang";

    @Field.String
    @Field(required = true, displayName = "语言名称", translate = true)
    private String name;

    @Field.String
    @Field(required = true, displayName = "语言ISO代码", summary = "国际化编码")
    private String isoCode;

    @Deprecated
    @Field.many2one
    @Field(displayName = "图标")
    private PamirsFile icon;

    @Field.many2one
    @Field(displayName = "图标")
    private ResourceIcon resourceIcon;

    @Field.Enum
    @Field(required = true, displayName = "书写习惯")
    private DirectionEnum direction;

    @Field.Enum
    @Field(required = true, displayName = "一周开始日")
    private WeekStartEnum weekStart;

    @Deprecated
    @Field.Boolean
    @Field(required = true, displayName = "语言包安装状态")
    private Boolean installState;

    @Field.String
    @Field(required = true, displayName = "小数格式")
    private String decimalPoint;

    @Field.String
    @Field(required = true, displayName = "整数格式")
    private String thousandsSep;

    @Field.Enum
    @Field(displayName = "激活状态", defaultValue = "ACTIVE")
    private ActiveEnum active;

    @Field.Enum
    @Field(required = true, displayName = "日历")
    private CalendarTypeEnum calendarType;

    //fixme zl 基准时区挪到ResourceConfig中
    // @Field.Enum
//    @Field(  required = true, displayName = "基准时区")
//    private TimeZoneTypeEnum baseTimezoneType;

    @Field.Enum
    @Field(required = true, displayName = "时区")
    private TimeZoneTypeEnum timezoneType;

    @Field.Enum
    @Field(displayName = "地址格式")
    private List<AddressTypeEnum> addressTypes;

    @Field.String
    @Field(displayName = "地址格式", summary = "运行时实际使用值")
    private String addressFormat;

    @Deprecated
    @Field.String
    @Field(required = true, displayName = "日期格式")
    private String dateFormat;

    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "日期格式", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private ResourceDateFormat resourceDateFormat;

    @Deprecated
    @Field.String
    @Field(required = true, displayName = "时间格式")
    private String timeFormat;

    @Field.many2one
    @Field(displayName = "时间格式", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Relation(store = false)
    private ResourceTimeFormat resourceTimeFormat;

    @Field.String
    @Field(displayName = "数字分组规则", defaultValue = "3")
    private String groupingRule;

    @Field.Boolean
    @Field(store = NullableBoolEnum.FALSE, displayName = "当前用户语言")
    private Boolean userCurrentLang;

}

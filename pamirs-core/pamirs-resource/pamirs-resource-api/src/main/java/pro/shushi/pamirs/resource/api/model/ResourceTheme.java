package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.resource.api.enmu.BlocksLayoutEnum;
import pro.shushi.pamirs.resource.api.enmu.MenuLayoutEnum;
import pro.shushi.pamirs.resource.api.enmu.PopupTypeEnum;

@Model.model(ResourceTheme.MODEL_MODEL)
@Model.Advanced(name = "resourceTheme")
@Model(displayName = "主题", labelFields = "name")
public class ResourceTheme extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceTheme";

    @Field.String
    @Field(required = true, displayName = "主题名称")
    private String name;

//    @Field(ctype = "text", displayName = "主题定义")
//    private String scheme;

    @Field.Boolean
    @Field(displayName = "是否默认主题")
    private Boolean isDefault;

    @Field.String
    @Field(displayName = "作者")
    private String author;

    @Field.Enum
    @Field(displayName = "弹窗方式")
    private PopupTypeEnum popupType;

    @Field.Enum
    @Field(displayName = "菜单布局")
    private MenuLayoutEnum menuLayout;

    @Field.Enum
    @Field(displayName = "Blocks布局")
    private BlocksLayoutEnum blocksLayout;

    @Field.String
    @Field(displayName = "主色")
    private String mainColor;

    @Field.String
    @Field(displayName = "页面底色")
    private String pageBackGroundColor;

    @Field.String
    @Field(displayName = "Header色")
    private String headerColor;

    @Field.String
    @Field(displayName = "Footer色")
    private String footerColor;

    @Field.String
    @Field(displayName = "button")
    private String buttonStyle;

    @Field.String
    @Field(displayName = "header")
    private String headerStyle;

    @Field.String
    @Field(displayName = "footer")
    private String footerStyle;

    @Field.String
    @Field(displayName = "菜单")
    private String menuStyle;

    @Field.String
    @Field(displayName = "list")
    private String listStyle;

    @Field.String
    @Field(displayName = "时间选择器")
    private String timeSelectorStyle;

    @Field.String
    @Field(displayName = "颜色选择器")
    private String colorSelectorStyle;

    @Field.Text
    @Field(displayName = "主题配置")
    private String themeOptions;
}

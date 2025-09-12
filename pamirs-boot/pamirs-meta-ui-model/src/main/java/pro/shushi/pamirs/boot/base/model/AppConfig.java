package pro.shushi.pamirs.boot.base.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.enmu.*;
import pro.shushi.pamirs.boot.base.tmodel.MultiTabTheme;
import pro.shushi.pamirs.boot.base.tmodel.SideBarTheme;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Map;

/**
 * 应用配置
 *
 * @author d@shushi.pro
 * @version 2.2.0
 * date 2021/5/8 1:11 下午
 */
@Base
@Model.Advanced(priority = 100)
@Model.model(AppConfig.MODEL_MODEL)
@Model(displayName = "应用配置", summary = "应用配置")
public class AppConfig extends IdModel {

    private static final long serialVersionUID = 8963093715033489901L;

    public static final String MODEL_MODEL = "base.AppConfig";

    @Base
    @Field(displayName = "域")
    private AppConfigScopeEnum scope;

    @Base
    @Field(displayName = "配置编码", unique = true)
    private String code;

    @Base
    @Field(displayName = "公司编码")
    private String companyCode;

    @Base
    @Field(displayName = "入口应用编码")
    private String app;

    //    浏览器
    @Base
    @Field.String(size = 512)
    @Field(displayName = "网页图标")
    private String favicon;

    @Base
    @Field(displayName = "浏览器的Title")
    private String browserTitle;

    //    企业
    @Base
    @Field(displayName = "企业名称")
    private String partnerName;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "企业官网")
    private String officialWebsite;

    @Base
    @Field(displayName = "企业slogan")
    private String slogan;

    @Base
    @Field(displayName = "备案号")
    private String icpDesc;

    //    logo
    @Base
    @Field.String(size = 512)
    @Field(displayName = "标志")
    private String logo;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "折叠标志")
    private String appSideLogo;

    @Deprecated
    @Base
    @Field.String(size = 512)
    @Field(displayName = "小标志")
    private String smallLogo;

    // 登录
    @Base
    @Field.String(size = 512)
    @Field(displayName = "登录页标志")
    private String loginPageLogo;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "登录页背景")
    private String loginBackground;

    @Base
    @Field.Enum
    @Field(displayName = "登录页布局类型")
    private AppConfigLoginLayoutTypeEnum loginLayoutType;

    // 主题
    @Base
    @Field.Enum
    @Field(displayName = "主题模式")
    private AppConfigModeEnum mode;

    @Base
    @Field.Enum
    @Field(displayName = "主题风格")
    private AppConfigThemeStyleEnum style;

    @Base
    @Field.Enum
    @Field(displayName = "尺寸")
    private AppConfigSizeEnum size;

    @Base
    @Field(displayName = "主题")
    private String theme;

    @Base
    @Field(displayName = "母版")
    private String mask;

    // 首页
    @Base
    @Field(displayName = "使用默认首页")
    private Boolean defaultHomePage;

    @Base
    @Field(displayName = "主页模型编码", invisible = true)
    private String homePageModel;

    @Base
    @Field(displayName = "主页动作名称", invisible = true)
    private String homePageName;

    @Base
    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "多tab栏样式", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private MultiTabTheme multiTabTheme;

    @Base
    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "侧边栏样式", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private SideBarTheme sideBarTheme;

    @Base
    @Field(displayName = "扩展配置")
    @Field.Advanced(columnDefinition = "LONGTEXT")
    private Map<String, Object> extend;

    public static String generateCode(AppConfigScopeEnum scope, String... codes) {
        return scope.value() + CharacterConstants.SEPARATOR_OCTOTHORPE
                + StringUtils.join(codes, CharacterConstants.SEPARATOR_OCTOTHORPE);
    }

}

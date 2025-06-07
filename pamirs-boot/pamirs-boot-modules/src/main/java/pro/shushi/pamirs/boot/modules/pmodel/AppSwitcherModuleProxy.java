package pro.shushi.pamirs.boot.modules.pmodel;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * @author shier
 * date  2021/5/26 10:59 上午
 */
@Base
@Model.model(AppSwitcherModuleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "AppSwitcher模块代理", labelFields = "displayName")
public class AppSwitcherModuleProxy extends UeModule {

    public static final String MODEL_MODEL = "base.AppSwitcherModuleProxy";

    @Field.Integer
    @Field(displayName = "应用首页视图id", store = NullableBoolEnum.FALSE)
    private Long homepageViewId;

    @Field.Enum
    @Field(displayName = "应用首页视图系统来源", store = NullableBoolEnum.FALSE)
    private SystemSourceEnum homepageViewSystemSource;

    @Field.Enum
    @Field(displayName = "应用是否收藏", store = NullableBoolEnum.FALSE, defaultValue = "ALL")
    private AppLikeEnum like;

    @Field.Relation(relationFields = {"homePageModel", "homePageName"}, referenceFields = {"model", "name"})
    @Field.many2one
    @Field(displayName = "URL主页")
    private UrlAction urlHomePage;
}

package pro.shushi.pamirs.apps.api.pmodel;

import pro.shushi.pamirs.apps.api.enmu.ModuleTypeEnum;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.boot.modules.enmu.AppStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * @author shier
 * date  2021/5/26 10:59 上午
 */
//@Base
@Model.model(AppsManagementModule.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY, unInheritedFunctions = {FunctionConstants.create, FunctionConstants.update, FunctionConstants.deleteWithFieldBatch})
@Model(displayName = "Apps管理module", labelFields = "displayName")
public class AppsManagementModule extends UeModule {

    private static final long serialVersionUID = -8374513625983937598L;

    public static final String MODEL_MODEL = "apps.AppsManagementModule";

    @Field.Enum
    @Field(displayName = "应用类型")
    private ModuleTypeEnum moduleType;

    @Field.many2one
    @Field(displayName = "绑定首页模型", summary = "绑定首页时用于下拉选择模型")
    private AppsModuleModelProxy bindHomePageModel;

    @Field.many2one
    @Field(displayName = "绑定首页页面")
    private View bindHomePageView;

    @Field.many2one
    @Field(displayName = "绑定首页菜单")
    private AppsModuleMenuProxy bindHomePageMenu;

    @Field.Enum
    @Field(displayName = "应用是否安装", store = NullableBoolEnum.FALSE, defaultValue = "ALL")
    private AppStatusEnum status;

    @Field.Enum
    @Field(displayName = "应用是否收藏", store = NullableBoolEnum.FALSE, defaultValue = "ALL")
    private AppLikeEnum like;

    @Field.Boolean
    @Field(displayName = "是否可升级", store = NullableBoolEnum.FALSE, defaultValue = "false")
    private Boolean canUpgrade;

    @Field.many2many
    @Field(displayName = "依赖模块列表")
    private List<AppsManagementModule> appsModuleDependencyList;

    @Field.many2many
    @Field(displayName = "上游模块列表")
    private List<AppsManagementModule> appsModuleUpstreamList;

    @Field.many2many
    @Field(displayName = "互斥模块列表")
    private List<AppsManagementModule> appsModuleExclusionList;

    @Field.Relation(relationFields = {"homePageModel", "homePageName"}, referenceFields = {"model", "name"})
    @Field.many2one
    @Field(displayName = "URL主页")
    private UrlAction urlHomePage;
}

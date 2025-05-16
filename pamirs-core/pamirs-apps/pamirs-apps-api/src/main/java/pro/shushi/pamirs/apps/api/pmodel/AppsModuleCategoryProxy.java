package pro.shushi.pamirs.apps.api.pmodel;

import pro.shushi.pamirs.apps.api.model.AppsModuleCategory;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.boot.modules.enmu.AppStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * @author drome
 * @date 2022/4/20下午7:59
 */
@Base
@Model(displayName = "应用分类代理", labelFields = {"name"})
@Model.model(AppsModuleCategoryProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AppsModuleCategoryProxy extends AppsModuleCategory {
    public static final String MODEL_MODEL = "apps.AppsModuleCategoryProxy";

    @Field.Integer
    @Field(displayName = "应用数量")
    private Long moduleNum;

    @Field.one2many
    @Field(displayName = "子分类列表")
    private List<AppsModuleCategoryProxy> children;

    @Field.one2many
    @Field(displayName = "模块")
    private List<AppsManagementModule> appModules;

    // 搜索应用模块联动 start ---
    @Field(displayName = "显示名称")
    private String displayName;

    @Field.Enum
    @Field(displayName = "应用是否安装", store = NullableBoolEnum.FALSE, defaultValue = "ALL")
    private AppStatusEnum status;

    @Field.Enum
    @Field(displayName = "应用是否收藏", store = NullableBoolEnum.FALSE, defaultValue = "ALL")
    private AppLikeEnum like;
    // 搜索应用模块联动 end ---

}

package pro.shushi.pamirs.apps.api.pmodel;

import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

@Base
@Model(displayName = "Apps模块菜单代理")
@Model.model(AppsModuleMenuProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class AppsModuleMenuProxy extends Menu {

    public static final String MODEL_MODEL = "apps.AppsModuleMenuProxy";

    /**
     * 支持使用树组件,且过滤条件默认指定了module
     */
    @Field.Relation(relationFields = {"parentName"}, referenceFields = {"name"})
    @Field(displayName = "上级菜单")
    private AppsModuleMenuProxy parentProxy;
}

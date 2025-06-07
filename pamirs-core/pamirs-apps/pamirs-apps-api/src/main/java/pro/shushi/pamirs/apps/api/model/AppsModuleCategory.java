package pro.shushi.pamirs.apps.api.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author shier
 * date  2021/6/21 2:38 下午
 */
@Base
@Model.model(AppsModuleCategory.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.STORE, unique = {"code"})
@Model(displayName = "Apps应用分类")
public class AppsModuleCategory extends ModuleCategory {

    public final static String MODEL_MODEL = "apps.AppsModuleCategory";


}

package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;

/**
 * 应用分类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(ModuleCategoryStatic.MODEL_MODEL)
@Model(displayName = "应用分类", summary = "应用分类")
public class ModuleCategoryStatic extends ModuleCategory {

    public final static String MODEL_MODEL = "static.ModuleCategory";

    private static final long serialVersionUID = 6147333798416389636L;
}

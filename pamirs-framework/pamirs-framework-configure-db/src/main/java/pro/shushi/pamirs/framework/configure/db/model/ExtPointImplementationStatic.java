package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;

/**
 * 扩展点实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.Ds(ModuleConstants.MODULE_BASE)// fix dddd
@Model.model(ExtPointImplementationStatic.MODEL_MODEL)
@Model(displayName = "扩展点实现", summary = "扩展点实现")
public class ExtPointImplementationStatic extends ExtPointImplementation {

    public final static String MODEL_MODEL = "static.ExtPointImplementation";

    private static final long serialVersionUID = -3422547029422815684L;

}

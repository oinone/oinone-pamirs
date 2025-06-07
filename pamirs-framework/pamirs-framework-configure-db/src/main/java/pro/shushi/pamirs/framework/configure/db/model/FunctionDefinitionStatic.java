package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 函数定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(FunctionDefinitionStatic.MODEL_MODEL)
@Model(displayName = "函数定义", summary = "函数定义")
public class FunctionDefinitionStatic extends FunctionDefinition {

    public final static String MODEL_MODEL = "static.Function";

    private static final long serialVersionUID = -8886218218804082713L;

}

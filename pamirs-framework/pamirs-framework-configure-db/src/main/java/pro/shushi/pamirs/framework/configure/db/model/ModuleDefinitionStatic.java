package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 模块静态定义（带序列化list字段）
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(ModuleDefinitionStatic.MODEL_MODEL)
@Model(displayName = "模块", summary = "模块")
public class ModuleDefinitionStatic extends ModuleDefinition {

    private static final long serialVersionUID = -7537738516594691123L;

    public final static String MODEL_MODEL = "static.ModuleDefinition";

}

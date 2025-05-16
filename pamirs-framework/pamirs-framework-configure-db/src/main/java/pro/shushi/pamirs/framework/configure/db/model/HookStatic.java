package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.fun.Hook;

/**
 * 拦截器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(HookStatic.MODEL_MODEL)
@Model(displayName = "拦截器", summary = "拦截器")
public class HookStatic extends Hook {

    public final static String MODEL_MODEL = "static.Hook";

    private static final long serialVersionUID = -710255390705990952L;
}

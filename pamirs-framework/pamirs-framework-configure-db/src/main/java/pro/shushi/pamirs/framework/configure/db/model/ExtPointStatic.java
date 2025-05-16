package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;

/**
 * 扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(ExtPointStatic.MODEL_MODEL)
@Model(displayName = "扩展点", summary = "扩展点")
public class ExtPointStatic extends ExtPoint {

    public final static String MODEL_MODEL = "static.ExtPoint";

    private static final long serialVersionUID = 4613250694112303507L;

}

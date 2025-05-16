package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 字段定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(ModelFieldStatic.MODEL_MODEL)
@Model(displayName = "字段定义", summary = "字段定义")
public class ModelFieldStatic extends ModelField {

    public final static String MODEL_MODEL = "static.ModelField";

    private static final long serialVersionUID = 211406947820826489L;

}

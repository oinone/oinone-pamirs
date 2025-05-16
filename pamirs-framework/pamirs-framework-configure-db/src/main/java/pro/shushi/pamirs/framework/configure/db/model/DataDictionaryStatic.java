package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

/**
 * 数据字典
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(DataDictionaryStatic.MODEL_MODEL)
@Model(displayName = "数据字典", summary = "数据字典")
public class DataDictionaryStatic extends DataDictionary {

    public final static String MODEL_MODEL = "static.DataDictionary";

    private static final long serialVersionUID = 7860583974929990519L;

}

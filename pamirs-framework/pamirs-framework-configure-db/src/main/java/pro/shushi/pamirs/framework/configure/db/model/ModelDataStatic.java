package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.ModelData;

/**
 * 安装注册表
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(ModelDataStatic.MODEL_MODEL)
@Model(displayName = "安装注册表", summary = "安装注册表")
public class ModelDataStatic extends ModelData {

    private static final long serialVersionUID = 3290295594583541570L;

    public final static String MODEL_MODEL = "static.ModelData";

}

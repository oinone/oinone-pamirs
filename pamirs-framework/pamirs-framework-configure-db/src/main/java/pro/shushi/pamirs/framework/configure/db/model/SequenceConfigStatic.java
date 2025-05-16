package pro.shushi.pamirs.framework.configure.db.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

/**
 * 序列生成配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.model(SequenceConfigStatic.MODEL_MODEL)
@Model(displayName = "序列生成配置", summary = "序列生成配置")
public class SequenceConfigStatic extends SequenceConfig {

    public final static String MODEL_MODEL = "static.SequenceConfig";

    private static final long serialVersionUID = -2671229939053779737L;

}

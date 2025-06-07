package pro.shushi.pamirs.sid.model;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

/**
 * 主键生成工作节点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Static(module = ModuleConstants.MODULE_BASE)
@Model.Advanced
@Model.model(WorkerNodeStatic.MODEL_MODEL)
@Model(displayName = "主键生成工作节点", summary = "主键生成工作节点")
public class WorkerNodeStatic extends WorkerNode {

    public final static String MODEL_MODEL = "static.WorkerNode";

    private static final long serialVersionUID = -6629207525850039490L;
}

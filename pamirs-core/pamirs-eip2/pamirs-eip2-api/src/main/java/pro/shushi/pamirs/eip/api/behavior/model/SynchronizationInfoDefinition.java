package pro.shushi.pamirs.eip.api.behavior.model;

import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

@Model.Advanced(unique = {"interfaceName"})
@Model.model(SynchronizationInfoDefinition.MODEL_MODEL)
@Model(displayName = "同步服务定义", labelFields = "displayName")
public class SynchronizationInfoDefinition extends IdModel implements IDataStatus {

    public static final String MODEL_MODEL = "pamirs.eip.SynchronizationInfoDefinition";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "模块")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field.String
    @Field(displayName = "模块编码", required = true)
    private String module;

    @Field.String
    @Field(displayName = "模型", summary = "模型")
    private String modelModel;

    @Field.String
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Field.String
    @Field(displayName = "服务唯一标记", summary = "模型内唯一", required = true)
    private String interfaceName;

    @Base
    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

}

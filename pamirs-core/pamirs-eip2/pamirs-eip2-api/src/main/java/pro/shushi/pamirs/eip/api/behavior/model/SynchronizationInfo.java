package pro.shushi.pamirs.eip.api.behavior.model;

import pro.shushi.pamirs.eip.api.behavior.enmu.SynchronizationStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(SynchronizationInfo.MODEL_MODEL)
@Model(displayName = "同步信息")
public class SynchronizationInfo extends IdModel {

    private static final long serialVersionUID = -183976206641864911L;

    public static final String MODEL_MODEL = "pamirs.eip.SynchronizationInfo";

    @Field.String
    @Field(displayName = "ids", summary = "ids,逗号分隔")
    private String ids;

    @Field.String
    @Field(displayName = "模型", summary = "模型")
    private String modelModel;

    @Field.String
    @Field(displayName = "服务唯一标记", summary = "模型内唯一")
    private String interfaceName;

    @Field.Enum
    @Field(displayName = "同步状态", defaultValue = "PENDING", required = true)
    private SynchronizationStatusEnum synchronizationStatus;

    @Field.String
    @Field(displayName = "显示名称", required = true)
    private String displayName;

    @Field.String
    @Field(displayName = "同步信息", required = true)
    private String message;

}

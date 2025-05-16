package pro.shushi.pamirs.eip.api.behavior.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Model.model(SynchronizationInfoTransient.MODEL_MODEL)
@Model(displayName = "同步信息临时模型")
public class SynchronizationInfoTransient extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.SynchronizationInfoTransient";

    @Field.String
    @Field(displayName = "ids", summary = "ids,逗号分隔")
    private String ids;

    @Field.String
    @Field(displayName = "模型", summary = "模型")
    private String modelModel;

    @Field.String
    @Field(displayName = "服务唯一标记", summary = "模型内唯一,前端用", required = true)
    private String interfaceNames;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "接口列表")
    List<SynchronizationInfoDefinition> definitions;

}

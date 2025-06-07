package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.business.api.model.BizCodeModel;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

/**
 * EipIntegrate 集成应用
 *
 * @author yakir on 2023/04/04 16:22.
 */
@Model.model(EipIntegrate.MODEL_MODEL)
@Model.Advanced(unique = {"connectId"})
@Model(displayName = "集成应用应用", labelFields = "name", summary = "用于系统与系统间的数据交互")
@Model.Code(sequence = "ORDERLY_SEQ", prefix = "APP", size = 5, initial = 1)
public class EipIntegrate extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = 656926688396489331L;

    public final static String MODEL_MODEL = "eip.EipIntegrate";

    @Field(displayName = "连接器Id")
    @Field.Integer
    private Long connectId;

    @Field.String
    @Field(displayName = "应用名称")
    private String name;

    @Field.Text
    @Field(displayName = "应用简介")
    private String description;

    @Field.String(size = 512)
    @Field(displayName = "应用Logo")
    private String logo;

    @Field(displayName = "业务域Code")
    @Field.String
    private String groupCode;

    @Field(displayName = "业务域")
    @Field.many2one
    @Field.Relation(relationFields = "groupCode", referenceFields = "code")
    private EipConnGroup group;

    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Field(displayName = "集成接口列表")
    @Field.Relation(relationFields = "id", referenceFields = "integrateId")
    private List<EipIntegrationInterface> interfaceList;
}

package pro.shushi.pamirs.eip.api.model.scene;

import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;

/**
 * @author drome
 * @date 2021/7/3011:54 上午
 */
@Base
@Model.model(EipSceneDefinition.MODEL_MODEL)
@Model.Advanced(unique = {"sceneName"})
@Model(displayName = "场景定义", labelFields = "name")
public class EipSceneDefinition extends IdModel implements IDataStatus {

    public static final String MODEL_MODEL = "pamirs.eip.EipSceneDefinition";

    private static final long serialVersionUID = 99630901154464824L;


    @Base
    @Field.String
    @Field(displayName = "场景名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "场景技术名称", required = true)
    private String sceneName;

    @Base
    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    //    固定双节点. A获取数据, B处理数据
    @Base
    @Field.one2many
    @Field(displayName = "数据来源系统列表")
    @Field.Relation(relationFields = {"id", CharacterConstants.SEPARATOR_OCTOTHORPE + "dataSource" + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"sceneDefinitionId", "type"})
    private List<EipSceneNodeDefinition> dataSourceNodeDefinitions;

    @Base
    @Field.one2many
    @Field(displayName = "数据接收系统列表")
    @Field.Relation(relationFields = {"id", CharacterConstants.SEPARATOR_OCTOTHORPE + "dataTarget" + CharacterConstants.SEPARATOR_OCTOTHORPE}, referenceFields = {"sceneDefinitionId", "type"})
    private List<EipSceneNodeDefinition> dataTargetNodeDefinitions;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"sceneName"}, referenceFields = {"sceneName"})
    @Field(displayName = "场景实例列表")
    private List<EipSceneInstance> sceneInstances;
}

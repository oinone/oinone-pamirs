package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

@Deprecated
@Base
@Model.model(EipInterfaceAuthGroup.MODEL_MODEL)
@Model.Advanced(unique = {"name"})
@Model(displayName = "应用接口权限组", summary = "应用接口权限组:暂用于开放接口")
public class EipInterfaceAuthGroup extends IdModel implements IDataStatus {

    public static final String MODEL_MODEL = "pamirs.eip.EipInterfaceAuthGroup";

    @Base
    @Field.String
    @Field(displayName = "权限组名称", required = true)
    private String name;

    @Base
    @Field.Enum
    @Field(displayName = "接口类型", required = true, defaultValue = "OPEN")
    private InterfaceTypeEnum interfaceType;

    @Base
    @Field.Text
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

    @Base
    @Field.many2many(through = "OpenInterfaceRelGroup", relationFields = {"groupId"}, referenceFields = {"openInterfaceId"})
    @Field(displayName = "开放接口列表", summary = "开放接口列表")
    private List<EipOpenInterface> openInterfaceList;

    /**
     * 保留字段,暂不使用
     */
    @Base
    @Field.many2many(through = "IntegrationInterfaceRelGroup", relationFields = {"groupId"}, referenceFields = {"integrationInterfaceId"})
    @Field(displayName = "集成接口列表", summary = "集成接口列表")
    private List<EipIntegrationInterface> integrationInterfaceList;
}

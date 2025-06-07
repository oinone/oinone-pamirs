package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * @author yeshenyue on 2024/9/5 17:37.
 */
@Model.model(EipParamMapping.MODEL_MODEL)
@Model.Advanced(unique = {"model,viewName,actionName,interfaceName"})
@Model(displayName = "界面和Eip参数映射", labelFields = "name", summary = "已发布界面与集成接口映射关系")
public class EipParamMapping extends IdModel {

    private static final long serialVersionUID = -6156162464621762329L;

    public final static String MODEL_MODEL = "eip.EipParamMapping";

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "Header参数映射", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "TEXT")
    private List<EipParamMappingItem> headerMapping;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "Query参数映射", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "TEXT")
    private List<EipParamMappingItem> queryMapping;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "Path参数映射", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "TEXT")
    private List<EipParamMappingItem> pathMapping;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "Body参数映射", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "TEXT")
    private List<EipParamMappingItem> bodyMapping;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "响应参数映射", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "TEXT")
    private List<EipParamMappingItem> responseMapping;

    @Field.String
    @Field(displayName = "集成接口技术名称", required = true)
    private String interfaceName;

    @Field.many2one
    @Field.Relation(relationFields = {"interfaceName"}, referenceFields = {"interfaceName"})
    @Field(displayName = "集成接口")
    private EipIntegrationInterface integrationInterface;

    @Field.String
    @Field(displayName = "模型编码", required = true)
    private String model;

    @Field.String
    @Field(displayName = "视图名称", required = true)
    private String viewName;

    @Field.many2one
    @Field(displayName = "视图")
    @Field.Relation(relationFields = {"viewName", "model"}, referenceFields = {"name", "model"})
    private View view;

    @Field.String
    @Field(displayName = "服务器动作名称", required = true)
    private String actionName;

    @Field.many2one
    @Field.Relation(relationFields = {"model", "actionName"}, referenceFields = {"model", "name"}, domain = "actionType=eq=SERVER")
    @Field(displayName = "服务器动作")
    private ServerAction serverAction;

    @Field.Boolean
    @Field(displayName = "是否为DB连接器")
    private Boolean isDb;
}

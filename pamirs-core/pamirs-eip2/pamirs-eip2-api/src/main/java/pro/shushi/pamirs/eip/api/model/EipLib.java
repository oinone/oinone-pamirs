package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;

import java.util.List;

/**
 * 集成库
 *
 * @author Adamancy Zhang
 * @date 2021-01-05 11:34
 */
@Base
@Model.model(EipLib.MODEL_MODEL)
@Model(displayName = "集成库", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "L")
public class EipLib extends CodeModel {

    private static final long serialVersionUID = -6559515244371344754L;

    public static final String MODEL_MODEL = "pamirs.eip.EipLib";

    @Field.String
    @Field(displayName = "集成库名称", required = true)
    private String name;

    @Field.many2one
    @Field.Relation(relationFields = {"applicationCode"}, referenceFields = {"appKey"})
    @Field(displayName = "集成应用")
    private EipApplication application;

    @Field.String
    @Field(displayName = "集成应用编码", invisible = true)
    private String applicationCode;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"libCode"})
    @Field(displayName = "集成接口列表")
    private List<EipIntegrationInterface> integrationInterfaceList;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"libCode"})
    @Field(displayName = "开放接口列表")
    private List<EipOpenInterface> openInterfaceList;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"libCode"})
    @Field(displayName = "路由定义列表")
    private List<EipRouteDefinition> routeDefinitionList;
}

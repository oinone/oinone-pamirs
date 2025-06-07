package pro.shushi.pamirs.auth.view.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * 字段权限项
 *
 * @author Adamancy Zhang at 20:28 on 2024-01-16
 */
@Base
@Model.model(AuthFieldPermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "字段权限项")
public class AuthFieldPermissionItem extends AuthFieldPermission {

    private static final long serialVersionUID = -4199120331223020511L;

    public static final String MODEL_MODEL = "auth.AuthFieldPermissionItem";

    @Field.many2one
    @Field.Relation(relationFields = "model", referenceFields = "model")
    @Field(displayName = "权限模型")
    private ModelDefinition modelDefinition;

    @Field.Boolean
    @Field(displayName = "读权限")
    private Boolean permRead;

    @Field.Boolean
    @Field(displayName = "写权限")
    private Boolean permWrite;

    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field(displayName = "展示值")
    private String displayValue;

    @Field(displayName = "描述")
    private String description;

    @Field(displayName = "业务类型")
    private TtypeEnum ttype;
}

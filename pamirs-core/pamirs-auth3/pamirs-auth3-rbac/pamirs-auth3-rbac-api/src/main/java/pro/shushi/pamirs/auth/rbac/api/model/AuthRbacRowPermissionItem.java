package pro.shushi.pamirs.auth.rbac.api.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * RBAC行权限项
 *
 * @author Adamancy Zhang at 12:39 on 2024-01-09
 */
@Base
@Model.model(AuthRbacRowPermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "RBAC行权限项", labelFields = {"displayName"})
public class AuthRbacRowPermissionItem extends AuthRowPermission {

    private static final long serialVersionUID = 6665031906391940824L;

    public static final String MODEL_MODEL = "auth.AuthRbacRowPermissionItem";

    @Field.many2one
    @Field.Relation(relationFields = "model", referenceFields = "model")
    @Field(displayName = "权限模型")
    private ModelDefinition modelDefinition;

    /**
     * 兼容原有字段逻辑，与filter字段同步
     */
    @Deprecated
    @Field.Text
    @Field(displayName = "过滤条件表达式")
    private String domainExp;

    @Field.Boolean
    @Field(displayName = "读权限")
    private Boolean permRead;

    @Field.Boolean
    @Field(displayName = "写权限")
    private Boolean permWrite;

    @Field.Boolean
    @Field(displayName = "删除权限")
    private Boolean permDelete;
}

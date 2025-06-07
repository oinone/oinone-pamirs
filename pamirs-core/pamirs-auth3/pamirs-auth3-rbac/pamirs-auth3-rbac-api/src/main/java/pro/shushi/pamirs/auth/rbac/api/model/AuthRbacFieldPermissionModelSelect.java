package pro.shushi.pamirs.auth.rbac.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 字段权限模型选择
 *
 * @author Adamancy Zhang at 10:12 on 2024-08-23
 */
@Base
@Model.model(AuthRbacFieldPermissionModelSelect.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "字段权限模型选择")
public class AuthRbacFieldPermissionModelSelect extends ModelDefinition {

    private static final long serialVersionUID = -8198386056275175826L;

    public static final String MODEL_MODEL = "auth.AuthRbacFieldPermissionModel";

    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;

    @Field.Boolean
    @Field(displayName = "查询角色已配置权限的模型")
    private Boolean configured;
}

package pro.shushi.pamirs.auth.rbac.api.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * RBAC资源权限项
 *
 * @author Adamancy Zhang at 12:55 on 2023-12-23
 */
@Base
@Model.model(AuthRbacResourcePermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "RBAC资源权限项", labelFields = {"displayName"})
public class AuthRbacResourcePermissionItem extends AuthResourcePermission {

    private static final long serialVersionUID = 4394167622468952768L;

    public static final String MODEL_MODEL = "auth.AuthRbacResourcePermissionItem";

    @Field(displayName = "可访问")
    private Boolean canAccess;

    @Field(displayName = "可管理")
    private Boolean canManagement;

    @Field(displayName = "可设计")
    private Boolean canDesign;

    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field(displayName = "授权值")
    private Long authorizedValue;
}

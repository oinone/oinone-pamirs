package pro.shushi.pamirs.auth.rbac.api.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * RBAC自定义资源权限项
 *
 * @author Adamancy Zhang at 12:43 on 2024-08-09
 */
@Base
@Model.model(AuthRbacCustomResourcePermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "RBAC自定义资源权限项")
public class AuthRbacCustomResourcePermissionItem extends AuthResourcePermission {

    private static final long serialVersionUID = 6076764797775105253L;

    public static final String MODEL_MODEL = "auth.AuthRbacCustomResourcePermissionItem";

    @Field(displayName = "资源名称")
    private String displayName;

    @Field(displayName = "可访问")
    private Boolean canAccess;

    @Field(displayName = "可管理")
    private Boolean canManagement;

}

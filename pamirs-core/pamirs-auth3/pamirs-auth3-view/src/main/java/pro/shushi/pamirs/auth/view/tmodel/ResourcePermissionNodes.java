package pro.shushi.pamirs.auth.view.tmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 资源权限节点批量操作
 *
 * @author Adamancy Zhang at 10:14 on 2024-01-23
 */
@Base
@Model.model(ResourcePermissionNodes.MODEL_MODEL)
@Model(displayName = "资源权限节点批量操作")
public class ResourcePermissionNodes extends TransientModel {

    private static final long serialVersionUID = -6947743623112867750L;

    public static final String MODEL_MODEL = "auth.ResourcePermissionNodes";

    @Field.many2many
    @Field(displayName = "角色")
    private List<AuthRole> roles;

    @Field.many2many
    @Field(displayName = "权限项列表")
    private List<ResourcePermissionNode> permissions;
}

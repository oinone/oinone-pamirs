package pro.shushi.pamirs.auth.view.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 资源权限项
 *
 * @author Adamancy Zhang at 12:55 on 2023-12-23
 */
@Base
@Model.model(AuthResourcePermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "资源权限项", labelFields = {"displayName"})
public class AuthResourcePermissionItem extends AuthResourcePermission {

    private static final long serialVersionUID = -9053702987429112539L;

    public static final String MODEL_MODEL = "auth.AuthResourcePermissionItem";

    @Field(displayName = "可访问")
    private Boolean canAccess;

    @Field(displayName = "可管理")
    private Boolean canManagement;

    @Field(displayName = "可设计")
    private Boolean canDesign;
}

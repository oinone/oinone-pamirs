package pro.shushi.pamirs.auth.view.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 动作权限项
 *
 * @author Adamancy Zhang at 21:39 on 2024-01-16
 */
@Base
@Model.model(AuthActionPermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "动作权限项")
public class AuthActionPermissionItem extends AuthResourcePermission {

    private static final long serialVersionUID = 5457968116927723641L;

    public static final String MODEL_MODEL = "auth.AuthActionPermissionItem";

    @Field(displayName = "菜单名称")
    private String menuName;

    @Field(displayName = "可访问")
    private Boolean canAccess;
}

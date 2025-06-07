package pro.shushi.pamirs.auth.rbac.api.model;

import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * RBAC字段权限项
 *
 * @author Adamancy Zhang at 20:28 on 2024-01-16
 */
@Base
@Model.model(AuthRbacFieldPermissionItem.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "RBAC字段权限项")
public class AuthRbacFieldPermissionItem extends AuthFieldPermission {

    private static final long serialVersionUID = 6538853678110063722L;

    public static final String MODEL_MODEL = "auth.AuthRbacFieldPermissionItem";

    @Field(displayName = "读权限")
    @Field.Boolean
    private Boolean permRead;

    @Field(displayName = "写权限")
    @Field.Boolean
    private Boolean permWrite;

    @Field(displayName = "显示名称")
    private String displayName;

    @Field(displayName = "描述")
    private String description;

    @Field(displayName = "业务类型")
    private TtypeEnum ttype;
}

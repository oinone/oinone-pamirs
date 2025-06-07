package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 用户关联角色
 *
 * @author Adamancy Zhang at 14:23 on 2024-01-06
 */
@Base
@Model.Advanced(name = "userRoleRel", index = {"userId,source", "roleId,source"})
@Model.model(AuthUserRoleRel.MODEL_MODEL)
@Model(displayName = "用户关联角色")
public class AuthUserRoleRel extends BaseRelation implements AuthAuthorizationSource {

    private static final long serialVersionUID = 5716595089649781301L;

    public static final String MODEL_MODEL = "auth.UserRoleRel";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "用户ID")
    protected Long userId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "角色ID")
    protected Long roleId;

    @Field.Enum
    @Field(displayName = "授权来源", defaultValue = "MANUAL", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true", invisible = true)
    private Boolean active;
}

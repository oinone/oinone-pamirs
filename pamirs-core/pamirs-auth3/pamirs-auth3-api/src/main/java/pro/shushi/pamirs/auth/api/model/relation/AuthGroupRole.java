package pro.shushi.pamirs.auth.api.model.relation;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseRelation;

/**
 * 权限组关联角色
 *
 * @author Adamancy Zhang at 21:34 on 2024-04-03
 */
@Base
@Model.model(AuthGroupRole.MODEL_MODEL)
@Model(displayName = "权限组关联角色")
public class AuthGroupRole extends BaseRelation {

    private static final long serialVersionUID = -2928119983283240884L;

    public static final String MODEL_MODEL = "auth.AuthGroupRole";

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "角色ID")
    private Long roleId;
}

package pro.shushi.pamirs.auth.view.pmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 权限角色代理模型
 *
 * @author Adamancy Zhang at 10:15 on 2023-11-29
 */
@Base
@Model.model(AuthRoleProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "权限角色代理", labelFields = {"name"})
public class AuthRoleProxy extends AuthRole {

    private static final long serialVersionUID = -7197988739374379925L;

    public static final String MODEL_MODEL = "auth.AuthRoleProxy";

//    @JSONField(serialize = false)
//    @Field.many2many(through = AuthUserRoleRel.MODEL_MODEL, relationFields = "roleId", referenceFields = "userId", pageSize = Integer.MAX_VALUE)
//    @Field(displayName = "关联用户")
//    private List<PamirsUser> users;
}

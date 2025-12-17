package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

/**
 * 权限组
 *
 * @author Adamancy Zhang at 12:19 on 2024-01-09
 */
@Base
@Model.model(AuthGroup.MODEL_MODEL)
@Model.Advanced(unique = {"name"}, index = {"type,source"})
@Model(displayName = "权限组", labelFields = {"displayName"})
public class AuthGroup extends IdModel {

    private static final long serialVersionUID = 7576103365063015274L;

    public static final String MODEL_MODEL = "auth.AuthGroup";

//    @Field.String
//    @Field(displayName = "编码")
//    private String code;

    @Field.String(size = 256)
    @Field(displayName = "名称")
    private String name;

    @Field.Enum
    @Field(displayName = "权限组类型", defaultValue = "RUNTIME")
    private AuthGroupTypeEnum type;

    @Field.Enum
    @Field(displayName = "权限组来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true")
    private Boolean active;

    @Field.String
    @Field(displayName = "显示名称")
    private String displayName;

    @Field.Text
    @Field(displayName = "说明")
    private String comment;

    @Field.many2many(through = AuthGroupRole.MODEL_MODEL, relationFields = {"groupId"}, referenceFields = {"roleId"})
    @Field.Relation(relationFields = {"id"}, referenceFields = {"id"})
    @Field(displayName = "角色")
    private List<AuthRole> roles;
}

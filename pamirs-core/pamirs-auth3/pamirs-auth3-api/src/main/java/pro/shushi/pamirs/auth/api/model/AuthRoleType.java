package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.core.common.behavior.IUserNameModel;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 角色类型
 *
 * @author Adamancy Zhang at 14:21 on 2024-01-06
 */
@Model.model(AuthRoleType.MODEL_MODEL)
@Model.Advanced(unique = {"code", "name"})
@Model(displayName = "角色类型", labelFields = "name")
public class AuthRoleType extends IdModel implements AuthAuthorizationSource, IUserNameModel {

    private static final long serialVersionUID = 2522758864661333723L;

    public static final String MODEL_MODEL = "auth.AuthRoleType";

    @Field.String(size = 64)
    @Field(displayName = "类型编码")
    private String code;

    @Field.String
    @Field(displayName = "类型名称", translate = true, required = true)
    private String name;

    @Field.Text
    @Field(displayName = "描述", translate = true)
    private String description;

    @Field.Enum
    @Field(displayName = "角色类型来源", invisible = true, defaultValue = "MANUAL")
    private AuthorizationSourceEnum source;

    @Field.String
    @Field(displayName = "创建人", store = NullableBoolEnum.FALSE, translate = true)
    private String createUserName;

    @Field.String
    @Field(displayName = "修改人", store = NullableBoolEnum.FALSE, translate = true)
    private String writeUserName;

    @Deprecated
    @Field.String
    @Field(displayName = "类型", translate = true, required = true)
    private String type;
}

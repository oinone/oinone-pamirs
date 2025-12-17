package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.behavior.AuthAuthorizationSource;
import pro.shushi.pamirs.auth.api.enmu.PermissionDataSourceEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.boot.web.constants.BusinessModelConstants;
import pro.shushi.pamirs.core.common.behavior.IUserNameModel;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 角色
 *
 * @author Adamancy Zhang at 14:35 on 2024-01-06
 */
@Model.Advanced(unique = {"code", "name"})
@Model.model(AuthRole.MODEL_MODEL)
@Model(displayName = "角色", labelFields = "name")
public class AuthRole extends IdModel implements AuthAuthorizationSource, IUserNameModel {

    private static final long serialVersionUID = -6792252804729589484L;

    public static final String MODEL_MODEL = BusinessModelConstants.ROLE;

    @Field.String(size = 64)
    @Field(displayName = "编码")
    private String code;

    @Field.String
    @Field(displayName = "名称", translate = true, required = true)
    private String name;

    @Field.Text
    @Field(displayName = "描述", translate = true)
    private String description;

    @Field.Enum
    @Field(displayName = "角色来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.many2one
    @Field.Relation(relationFields = {"roleTypeCode"}, referenceFields = {"code"})
    @Field(displayName = "角色类型")
    private AuthRoleType roleType;

    @Field.String
    @Field(displayName = "角色类型编码")
    private String roleTypeCode;

    @Field.Boolean
    @Field(displayName = "激活状态", defaultValue = "true", required = true)
    private Boolean active;

    @Base
    @Field.String
    @Field(displayName = "创建人", store = NullableBoolEnum.FALSE, translate = true)
    private String createUserName;

    @Base
    @Field.String
    @Field(displayName = "修改人", store = NullableBoolEnum.FALSE, translate = true)
    private String writeUserName;

    /**
     * @deprecated please using AuthRole#source
     */
    @Deprecated
    @Field.Enum
    @Field(displayName = "数据来源", summary = "1.系统级别 2.自定义 3.业务内置", defaultValue = "CUSTOM")
    private PermissionDataSourceEnum permissionDataSource;

    public static <T extends AuthRole> T transfer(AuthRole origin, T target) {
        target.setId(origin.getId());
        target.setCode(origin.getCode());
        target.setName(origin.getName());
        target.setDescription(origin.getDescription());
        target.setSource(origin.getSource());
        target.setRoleType(origin.getRoleType());
        target.setRoleTypeCode(origin.getRoleTypeCode());
        target.setActive(origin.getActive());
        target.setPermissionDataSource(origin.getPermissionDataSource());
        return target;
    }
}

package pro.shushi.pamirs.auth.api.model;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.core.common.behavior.IUserNameModel;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 自定义权限组
 *
 * @author Adamancy Zhang at 11:37 on 2024-08-09
 */
@Base
@Model.model(AuthCustomGroup.MODEL_MODEL)
@Model.Advanced(unique = {"code"})
@Model(displayName = "自定义权限组")
public class AuthCustomGroup extends IdModel implements IUserNameModel {

    private static final long serialVersionUID = 7490444973656205603L;

    public static final String MODEL_MODEL = "auth.AuthCustomGroup";

    @Field.String(size = 64)
    @Field(displayName = "编码")
    private String code;

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.Enum
    @Field(displayName = "权限组来源", invisible = true)
    private AuthorizationSourceEnum source;

    @Field.Text
    @Field(displayName = "说明")
    private String comment;

    @Field.String(size = 64)
    @Field(displayName = "上级分组编码")
    private String parentCode;

    @Field.many2one
    @Field.Relation(relationFields = {"parentCode"}, referenceFields = {"code"})
    @Field(displayName = "上级分组")
    private AuthCustomGroup parent;

    @Field.Text
    @Field(displayName = "资源路径")
    private String path;

    @Base
    @Field.String
    @Field(displayName = "创建人", store = NullableBoolEnum.FALSE, translate = true)
    private String createUserName;

    @Base
    @Field.String
    @Field(displayName = "修改人", store = NullableBoolEnum.FALSE, translate = true)
    private String writeUserName;
}

package pro.shushi.pamirs.user.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;

import java.util.Map;

@Base
@Model.model(UserListFieldPreferStore.MODEL_MODEL)
@Model.Advanced(name = "userListFieldPreferStore", unique = "partnerId,resModel,viewName")
@Model(displayName = "用户列表字段偏好")
public class UserListFieldPreferStore extends IdModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "user.UserListFieldPreferStore";

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.Integer
    @Field(displayName = "用户ID")
    private Long partnerId;

    @Validation(check = checkModelModel)
    @Field.String
    @Field(displayName = "模型")
    private String resModel;

    @Field.Text
    @Field(displayName = "隐藏字段")
    private String fieldPrefer;

    @Field.Text
    @Field(displayName = "字段顺序")
    private String fieldOrder;

    @Field.Text
    @Field(displayName = "字段宽度")
    private String fieldWidth;

    @Field.Text
    @Field(displayName = "字段左侧固定")
    private String fieldLeftFixed;

    @Field.Text
    @Field(displayName = "字段右侧固定")
    private String fieldRightFixed;

    @Field.String
    @Field(displayName = "页面")
    private String viewName;

    @Field(displayName = "扩展配置")
    @Field.Advanced(columnDefinition = "LONGTEXT")
    private Map<String, Object> extend;
}

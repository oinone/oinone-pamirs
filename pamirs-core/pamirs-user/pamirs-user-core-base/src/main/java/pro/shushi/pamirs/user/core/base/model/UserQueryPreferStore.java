package pro.shushi.pamirs.user.core.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;

@Model.model(UserQueryPreferStore.MODEL_MODEL)
@Model.Advanced(name = "userQueryPreferStore", index = {"partnerId,resModel,resViewName"})
@Model(displayName = "用户查询偏好")
public class UserQueryPreferStore extends IdModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "user.UserQueryPreferStore";

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

    @Field.String
    @Field(displayName = "页面")
    private String resViewName;

    @Field.Text
    @Field(displayName = "查询偏好")
    private String searchPrefer;

    @Field.Text
    @Field(displayName = "分组偏好")
    private String groupPrefer;

    @Field.Text
    @Field(displayName = "过滤")
    private String filter;

}

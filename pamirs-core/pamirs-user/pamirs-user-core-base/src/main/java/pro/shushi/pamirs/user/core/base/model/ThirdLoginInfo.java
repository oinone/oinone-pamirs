package pro.shushi.pamirs.user.core.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

@Model.Advanced(name = "thirdLoginInfo")
@Model(displayName = "三方登录种类")
@Model.model("user.ThirdLoginInfo")
public class ThirdLoginInfo extends CodeModel {

    @Field.String
    @Field(displayName = "登录类型")
    private String thirdLoginType;

    @Field.Text
    @Field(displayName = "图标地址")
    private String iconUrl;

    @Field.Text
    @Field(displayName = "图标超链接")
    private String link;

    @Field.Boolean
    @Field(displayName = "是否激活")
    private Boolean active;

}

package pro.shushi.pamirs.user.core.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.Advanced(name = "DingTalkUserTransient")
@Model(displayName = "钉钉用户临时模型")
public class DingTalkUserTransient extends TransientModel {

    @Field.String
    @Field(displayName = "token")
    private String token;

    @Field.String
    @Field(displayName = "requestAuthCode")
    private String requestAuthCode;

}

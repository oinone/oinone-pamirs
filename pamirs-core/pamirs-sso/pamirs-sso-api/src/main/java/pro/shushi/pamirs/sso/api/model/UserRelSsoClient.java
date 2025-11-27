package pro.shushi.pamirs.sso.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(UserRelSsoClient.MODEL_MODEL)
@Model(displayName = "用户/SSO客户端关联表")
@Model.Advanced(index = {"tradeOrderCode","tradeOrderId"})
public class UserRelSsoClient extends IdModel {
    public static final String MODEL_MODEL = "sso.UserRelSsoClient";

    @Field.Integer
    @Field(displayName = "用户ID")
    private Long userId;

    @Field.String
    @Field(displayName = "SSO客户端ID")
    private String ssoClientId;
}

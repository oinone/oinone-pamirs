package pro.shushi.pamirs.user.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.user.api.enmu.UserThirdPartyTypeEnum;

import java.util.Date;

/**
 * @Author: haibo
 * @email: xf.z@shushi.pro
 * @Date: 2019/10/28 3:23 下午
 */
@Model(displayName = "用户三方登录")
@Model.Advanced(unique = {"openid,thirdPartyType", "unionId,thirdPartyType"}, index = {"userId,openid"})
@Model.model(PamirsUserThirdParty.MODEL_MODEL)
public class PamirsUserThirdParty extends IdModel {

    public static final String MODEL_MODEL = "user.PamirsUserThirdParty";

    @Field.many2one
    @Field(displayName = "用户")
    PamirsUser user;

    @Field.Integer
    @Field(displayName = "用户id")
    Long userId;

    @Field.Enum
    @Field(displayName = "三方登录类型")
    UserThirdPartyTypeEnum thirdPartyType;

    @Field.String
    @Field(displayName = "unionId")
    String unionId;

    @Field.String
    @Field(displayName = "openid")
    String openid;

    @Field.Date
    @Field(displayName = "上次登录时间")
    Date lastLoginTime;

}

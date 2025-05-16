package pro.shushi.pamirs.user.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 用户密码
 *
 * @author Adamancy Zhang at 09:59 on 2024-01-05
 */
@Base
@Model.model(PamirsPassword.MODEL_MODEL)
@Model.Advanced(unique = "userId")
@Model(displayName = "用户密码")
public class PamirsPassword extends IdModel {

    private static final long serialVersionUID = -6758052619352846692L;

    public static final String MODEL_MODEL = "user.PamirsPassword";

    @Field.Integer
    @Field(displayName = "用户ID")
    private Long userId;

    @Field.String
    @Field(displayName = "初始密码", summary = "初始密码")
    private String initialPassword;

    @Field.String
    @Field(displayName = "密码", summary = "密码")
    private String password;
}

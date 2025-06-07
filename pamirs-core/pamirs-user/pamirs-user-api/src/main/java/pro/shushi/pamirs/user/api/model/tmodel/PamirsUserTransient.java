package pro.shushi.pamirs.user.api.model.tmodel;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.GenderEnum;
import pro.shushi.pamirs.user.api.crypto.annotation.EncryptField;
import pro.shushi.pamirs.user.api.enmu.UserBehaviorEventEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.Date;
import java.util.List;

@Base
@Component
@Model.Advanced(name = "pamirsUserTransient")
@Model.model(PamirsUserTransient.MODEL_MODEL)
@Model(summary = "用户临时模型", displayName = "用户登录注册临时模型")
public class PamirsUserTransient extends TransientModel {

    private static final long serialVersionUID = 2017913606107124178L;

    public static final String MODEL_MODEL = "user.PamirsUserTransient";

    @Field.String
    @Field(displayName = "昵称")
    private String nickname;

    @Field.Date
    @Field(displayName = "出生日期")
    private Date birthday;

    @Field.Enum
    @Field(defaultValue = "NULL", displayName = "性别")
    private GenderEnum gender;

    @Field.String
    @Field(displayName = "真实姓名")
    private String realname;

    @Field.String
    @Field(displayName = "用户名", required = true)
    private String name;

    @EncryptField
    @Field.String
    @Field(displayName = "登录账号", required = true)
    private String login;

    @EncryptField
    @Field.String
    @Field(displayName = "密码", required = true)
    private String password;

    @EncryptField
    @Field.String
    @Field(displayName = "确认密码", required = true)
    private String confirmPassword;

    @EncryptField
    @Field.String
    @Field(displayName = "邮箱地址", required = true)
    private String email;

    @EncryptField
    @Field.String
    @Field(displayName = "旧密码", required = true)
    private String rawPassword;

    @EncryptField
    @Field.String
    @Field(displayName = "验证码", required = true)
    private String verificationCode;

    @EncryptField
    @Field.String
    @Field(displayName = "手机", required = true)
    private String phone;

    @EncryptField
    @Field.String
    @Field(displayName = "新手机", summary = "用于修改手机")
    private String newPhone;

    @Field.String
    @Field(displayName = "新手机验证码", summary = "用于修改手机")
    private String newVerificationCode;

    @Field.String
    @Field(displayName = "验证码类型", summary = "用于修改手机以及根据手机修改密码")
    private String msgType;

    @Field.String
    @Field(displayName = "新邮箱地址", summary = "用于修改邮箱")
    private String newEmail;

//    @Field.String
//    @Field(displayName = "邮箱确认码",summary = "用于修改邮箱")
//    private String emailConfirmation;
//
//    @Field.String
//    @Field(displayName = "新邮箱确认码",summary = "用于修改邮箱")
//    private String newEmailConfirmation;

    @Field.Boolean
    @Field(displayName = "是否需要跳转到首页")
    private Boolean needRedirect;

    @Field.many2one
    @Field(displayName = "跳转")
    private ViewAction redirect;

    @Field.many2one
    @Field(displayName = "用户")
    private PamirsUser user;

    @Field.String
    @Field(displayName = "错误信息")
    private String errorMsg;

    @Field.String
    @Field(displayName = "错误字段")
    private String errorField;

    @Field.Integer
    @Field(displayName = "错误代码")
    private Integer errorCode;

    @Field.Boolean
    @Field(displayName = "是否中断", invisible = true)
    private Boolean broken;

    @Field.String
    @Field(displayName = "token")
    private String token;

    @Field.String
    @Field(displayName = "图片验证码,三次输错")
    private String picCode;

    @Field.String
    @Field(displayName = "来源")
    private String source;

    @Field.Boolean
    @Field(displayName = "自动登录，7天免登录")
    private Boolean autoLogin;

    @Field.String
    @Field(displayName = "所属租户")
    private String tenant;

    @Field.Enum
    @Field(displayName = "用户行为事件")
    private UserBehaviorEventEnum userBehaviorEvent;

    @Field.many2one
    @Field(displayName = "可选租户列表")
    private List<PamirsTenantTransient> tenants;

    @Field.String
    @Field(displayName = "手机区号")
    private String phoneCode;

    // FIXME: zbh 20240612 登录和验证码的手机号PhoneCode没地方加，为了兼容旧版本，使用该字段作为PhoneCode进行传递。5.1版本使用phoneCode字段
    @Field(displayName = "邀请码")
    @Field.String
    private String inviteCode;

    @Field(displayName = "头像")
    @Field.String(size = 512)
    private String avatarUrl;

    @Field.String
    @Field(displayName = "openid")
    private String openid;

    @Field.String
    @Field(displayName = "unionId")
    private String unionId;

    public Boolean getBroken() {
        if (null == _d.get("broken")) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(_d.get("broken") + "");
    }

}

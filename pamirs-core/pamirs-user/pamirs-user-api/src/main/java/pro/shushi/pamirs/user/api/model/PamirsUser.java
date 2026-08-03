package pro.shushi.pamirs.user.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.user.AuthUser;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxDetail;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTable;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.DateTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.GenderEnum;
import pro.shushi.pamirs.resource.api.enmu.TimeZoneTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTheme;
import pro.shushi.pamirs.user.api.behavior.IUserNameModel;
import pro.shushi.pamirs.user.api.crypto.annotation.EncryptField;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Model.model(PamirsUser.MODEL_MODEL)
@Model.Advanced(name = "pamirsUser", unique = {"login"})
@Model(displayName = "用户表", labelFields = {"name"})
@Model.Code(sequence = "SEQ", prefix = "U", size = 8, initial = 8000000, isRandomStep = true)
public class PamirsUser extends CodeModel implements IUserNameModel, AuthUser {

    private static final long serialVersionUID = -6768624819281681592L;

    public static final String MODEL_MODEL = "user.PamirsUser";

    @Field.Enum
    @Field(displayName = "用户创建类型", summary = "用户通过何种方式创建的", defaultValue = "BACKSTAGE",
            required = true, invisible = true)
    private UserSignUpType signUpType;

    @Field.String
    @Field(displayName = "账户类型", summary = "账户类型", defaultValue = "MANUAL", invisible = true)
    private String userType;

    @Field.String
    @Field(displayName = "登录账号", summary = "登录账号", required = true)
    private String login;

    @Field.String
    @Field(displayName = "手机区号", summary = "手机区号")
    private String phoneCode;

    @Field.String
    @Field(displayName = "手机号", summary = "手机号")
    private String phone;

    @Field.String
    @Field(displayName = "邮箱地址", summary = "邮箱地址")
    private String email;

    /**
     * please using PamirsPassword
     */
    @Deprecated
    @Field.String
    @Field(displayName = "初始密码", summary = "初始密码")
    @EncryptField
    private String initialPassword;

    @Deprecated
    @Field.Boolean
    @Field(displayName = "是否是初始密码", summary = "是否是初始密码", store = NullableBoolEnum.FALSE)
    private Boolean isInitialPassword;

    /**
     * please using PamirsPassword
     */
    @Deprecated
    @Field.String
    @Field(displayName = "密码", summary = "密码", invisible = true)
    @EncryptField
    private String password;

    @Field.Enum
    @Field(displayName = "用户来源", defaultValue = "MANUAL", invisible = true)
    private UserSourceEnum source;

    @Field.Boolean
    @Field(displayName = "是否激活", summary = "账号是否激活,默认为激活状态", defaultValue = "true")
    private Boolean active;

    @Field.String
    @Field(displayName = "名称", translate = true)
    private String name;

    @Field.String
    @Field(displayName = "昵称", translate = true)
    private String nickname;

    @Field.String
    @Field(displayName = "真实姓名")
    private String realname;

    /**
     * 头像的大小让前端通过拼接后缀裁剪
     */
    @UxForm.FieldWidget(@UxWidget(widget = "UploadImg"))
    @UxTable.FieldWidget(@UxWidget(widget = "UploadImg"))
    @UxDetail.FieldWidget(@UxWidget(widget = "UploadImg"))
    @Field.String(size = 512)
    @Field(displayName = "头像链接")
    private String avatarUrl;

    @Deprecated
    @Field.many2one
    @Field(displayName = "大头像")
    private PamirsFile avatarBig;

    @Deprecated
    @Field.many2one
    @Field(displayName = "中等头像")
    private PamirsFile avatarMedium;

    @Field.many2one
    @Field(summary = "用户选择的系统主题")
    private ResourceTheme theme;

    @Field.many2one
    @Field(displayName = "语言")
    @Field.Relation(relationFields = {"langId"}, referenceFields = {"id"})
    private ResourceLang lang;

    @Field.Integer
    @Field(displayName = "语言ID", invisible = true)
    private Long langId;

    @Field.many2one
    @Field(displayName = "币种")
    private ResourceCurrency currency;

    @Field.many2one
    @Field(displayName = "国家")
    private ResourceCountry country;

    @Field.Enum
    @Field(displayName = "时区")
    private TimeZoneTypeEnum timeZoneType;

    @Field.String
    @Field(displayName = "联系电话", summary = "联系电话")
    private String contactPhone;

    @Field.String
    @Field(displayName = "联系邮箱", summary = "联系邮箱")
    private String contactEmail;

    @Deprecated
    @JSONField(serialize = false)
    @Field.many2many(through = AuthUserRoleRel.MODEL_MODEL, relationFields = "userId", referenceFields = "roleId", pageSize = Integer.MAX_VALUE)
    @Field(displayName = "角色")
    private List<AuthRole> roles;

    @Deprecated
    @JSONField(serialize = false)
    @Field.one2many
    @Field.Relation(referenceFields = "userId")
    @Field(displayName = "三方登录", invisible = true)
    private List<PamirsUserThirdParty> thirdParties;

    @Field.Date(type = DateTypeEnum.DATE, format = DateFormatEnum.DATE)
    @Field(displayName = "生日")
    private Date birthday;

    @Field.Enum
    @Field(displayName = "性别", summary = "男，女，保密")
    private GenderEnum gender;

    @Field.String
    @Field(displayName = "身份证号码")
    private String idCard;

    @Field.Date
    @Field(displayName = "注册日期")
    private Date regDate;

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "创建人名称", store = NullableBoolEnum.FALSE, priority = 201)
    private String createUserName;

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "修改人名称", store = NullableBoolEnum.FALSE, priority = 211)
    private String writeUserName;

    @Deprecated
    public <T extends PamirsUser> Optional<T> encodeUser() {
        throw new UnsupportedOperationException("Please using PasswordService");
    }

    @Deprecated
    @SuppressWarnings("rawtypes")
    public <T> Optional<T> encodeUser(T data) {
        throw new UnsupportedOperationException("Please using PasswordService");
    }
}

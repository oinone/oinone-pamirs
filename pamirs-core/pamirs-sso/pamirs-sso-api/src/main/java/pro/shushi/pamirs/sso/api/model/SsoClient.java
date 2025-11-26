package pro.shushi.pamirs.sso.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTableSearch;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.sso.api.enmu.SsoAuthTypeEnum;

/**
 * sso 客户端信息表
 */
@Model.model(SsoClient.MODEL_MODEL)
@Model(displayName = "客户端信息表", summary = "客户端信息表", labelFields = "name")
@Model.Advanced(unique = "clientId")
public class SsoClient extends IdModel {

    private static final long serialVersionUID = 4416200429553155392L;

    public static final String MODEL_MODEL = "sso.SsoClient";

    @UxTableSearch.FieldWidget(@UxWidget())
    @Field.String
    @Field(displayName = "客户端应用名", required = true)
    private String name;

    @UxTableSearch.FieldWidget(@UxWidget())
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "应用标识(client_id)")
    private String clientId;

    @Field.Text
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "应用密钥(client_secret)")
    private String clientSecret;

    @Field.Boolean
    @UxForm.FieldWidget(@UxWidget(widget = "Switch"))
    @Field(displayName = "是否开启SSO", defaultValue = "true", required = true)
    private Boolean enabled;

    @UxTableSearch.FieldWidget(@UxWidget())
    @Field.Enum
    @Field(displayName = "认证类型", required = true)
    private SsoAuthTypeEnum authType;

    @Field.Integer
    @Field(displayName = "access_token超时时间", defaultValue = "7200", required = true)
    private Long expiresIn;

    @Field.Integer
    @Field(displayName = "refresh_Token超时时间", defaultValue = "604800", required = true)
    private Long refreshTokenExpiresIn;

    @Field.Integer
    @Field(displayName = "auth_code超时时间", defaultValue = "600", required = true)
    private Long codeExpiresIn;

    @Field.String
    @Field(displayName = "应用回调地址", required = true)
    private String homepageUrl;

    @Field.String
    @Field(displayName = "设置退出地址")
    private String logoutUrl;

    @Field.Text
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "密钥", required = true, invisible = true)
    private String privateKey;

    @Field.Text
    @UxForm.FieldWidget(@UxWidget(readonly = "true"))
    @Field(displayName = "公钥", required = true, invisible = true)
    private String publicKey;

    @Field.Integer
    @Field(displayName = "缓存上一次对AccessToken失效时间", defaultValue = "600", required = true)
    private Long cacheTokenExpirationTime;

    @Field.Text
    @Field(displayName = "应用备注")
    private String remark;

}

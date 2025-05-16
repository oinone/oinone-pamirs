package pro.shushi.pamirs.sso.api.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;


/**
 * sso 客户端信息表
 */
@Model.model(SsoOauth2ClientDetails.MODEL_MODEL)
@Model(displayName = "客户端信息表", summary = "客户端信息表", labelFields = "name")
@UxRouteButton(
        action = @UxAction(name = "clientDetail", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = SsoOauth2ClientDetails.MODEL_MODEL, viewName = "ssoOauth2ClientDetailsForm", viewType = ViewTypeEnum.DETAIL, openType = ActionTargetEnum.ROUTER)
)
@UxRouteButton(
        action = @UxAction(name = "clientEdit", label = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = SsoOauth2ClientDetails.MODEL_MODEL, viewName = "ssoOauth2ClientDetailsForm", viewType = ViewTypeEnum.FORM, openType = ActionTargetEnum.ROUTER)
)
public class SsoOauth2ClientDetails extends IdModel {

    private static final long serialVersionUID = 4416200429553155392L;

    public static final String MODEL_MODEL = "sso.SsoOauth2ClientDetails";

    @Field.String
    @Field(displayName = "应用唯一标识", required = true)
    private String clientId;

    @Field.Text
    @Field(displayName = "应用唯一密钥", required = true)
    private String clientSecret;

    @Field.Boolean
    @Field(displayName = "是否开启SSO", defaultValue = "true", required = true)
    private Boolean enabled;

    @Field.String
    @Field(displayName = "设置授权回调地址", required = true)
    private String callbackUrl;


    @Field.Integer
    @Field(displayName = "Access_token 超时时间", defaultValue = "7200L", required = true)
    private Long expiresIn;

    @Field.Integer
    @Field(displayName = "Refresh_Token 超时时间", defaultValue = "604800L", required = true)
    private Long refreshTokenExpiresIn;

    @Field.Integer
    @Field(displayName = "设置 code 超时时间", defaultValue = "600L", required = true)
    private Long codeExpiresIn;

    @Field.String
    @Field(displayName = "跳转首页", required = true)
    private String homepageUrl;

    @Field.String
    @Field(displayName = "设置退出地址", required = true)
    private String logoutUrl;

    @Field.Text
    @Field(displayName = "密钥", required = true, invisible = true)
    private String privateKey;

    @Field.Text
    @Field(displayName = "公钥", required = true, invisible = true)
    private String publicKey;

    @Field.Integer
    @Field(displayName = "缓存上一次对AccessToken失效时间", defaultValue = "600L", required = true)
    private Long cacheTokenExpirationTime;

}

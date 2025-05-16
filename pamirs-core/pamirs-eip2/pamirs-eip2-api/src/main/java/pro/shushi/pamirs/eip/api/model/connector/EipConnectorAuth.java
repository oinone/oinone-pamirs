package pro.shushi.pamirs.eip.api.model.connector;

import pro.shushi.pamirs.eip.api.enmu.connector.ConnAuthType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * EipConnectorAuth
 *
 * @author yakir on 2023/03/29 16:12.
 */
@Base
@Model(displayName = "连接器认证")
@Model.model(EipConnectorAuth.MODEL_MODEL)
public class EipConnectorAuth extends IdModel {

    private static final long serialVersionUID = -7420129911422141124L;

    public final static String MODEL_MODEL = "designer.EipConnectorAuth";

    @Field(displayName = "连接器Id", store = NullableBoolEnum.FALSE)
    @Field.Integer
    private Long connectorId;

    @Field(displayName = "认证方式")
    @Field.Enum
    private ConnAuthType authType;

    /* basic */
    @Field(displayName = "帐号")
    @Field.String
    private String user;

    @Field(displayName = "密码")
    @Field.String
    private String password;

    /* oauth 2 */
    @Field(displayName = "跳转授权页")
    @Field.String
    private String authUrl;

    @Field(displayName = "OAuth2 Token")
    @Field.String
    private String token;

    /* common api key */
    @Field(displayName = "App Key")
    @Field.Text
    private String appKey;

    @Field(displayName = "App Key Id")
    @Field.String
    private String appKeyId;

    @Field(displayName = "App Secret")
    @Field.Text
    private String appSecret;

    @Field(displayName = "App Secret Id")
    @Field.String
    private String appSecretId;

}

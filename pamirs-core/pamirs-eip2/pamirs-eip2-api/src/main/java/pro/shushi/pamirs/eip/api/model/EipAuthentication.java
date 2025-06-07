package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 集成认证
 *
 * @author Adamancy Zhang at 19:17 on 2021-06-09
 */
@Base
@Model.model(EipAuthentication.MODEL_MODEL)
@Model.Advanced(unique = {"appKey"})
@Model(displayName = "集成认证", summary = "集成应用的认证信息", labelFields = "appKey")
public class EipAuthentication extends IdModel {

    private static final long serialVersionUID = 2511666239901140046L;

    public static final String MODEL_MODEL = "pamirs.eip.EipAuthentication";

    @Field.Enum
    @Field(displayName = "加密类型", defaultValue = "RSA", required = true)
    private EncryptTypeEnum encryptType;

    @Field.String(size = 32)
    @Field(displayName = "Application Key", required = true)
    private String appKey;

    @Field.String(size = 2048)
    @Field(displayName = "Application Secret", required = true)
    private String appSecret;

    @Field.Text
    @Field(displayName = "RSA私钥", required = true, invisible = true)
    private String privateKey;

    @Field.Text
    @Field(displayName = "RSA公钥", required = true)
    private String publicKey;
}

package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.core.common.enmu.EncryptTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenRateLimitPolicy;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

@Base
@Model.model(EipApplicationProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "应用代理模型", summary = "应用代理模型")
public class EipApplicationProxy extends EipApplication {

    public static final String MODEL_MODEL = "pamirs.eip.proxy.EipApplicationProxy";

    @Field.Enum
    @Field(displayName = "加密类型")
    private EncryptTypeEnum encryptType;

    @Field.String
    @Field(displayName = "加密密钥类型", translate = true)
    private String encryptKeyType;

    @Field.String(size = 2048)
    @Field(displayName = "Application Secret")
    private String appSecret;

    @Field.String(size = 2048)
    @Field(displayName = "RSA公钥")
    private String publicKey;

    @Field.one2many
    @Field.Relation(relationFields = {"code"}, referenceFields = {"applicationCode"})
    @Field(displayName = "API流控策略")
    private List<EipOpenRateLimitPolicy> rateLimitPolicyList;

    @Field(displayName = "请求预处理函数")
    @Field.many2one
    @Field.Relation(relationFields = {"requestDecryptNamespace", "requestDecryptFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition requestDecryptFunc;

    @Field(displayName = "响应预处理函数")
    @Field.many2one
    @Field.Relation(relationFields = {"responseEncryptionNamespace", "responseEncryptionFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition responseEncryptionFunc;
}

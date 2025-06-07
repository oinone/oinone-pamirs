package pro.shushi.pamirs.resource.api.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.RSAEncryptUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.resource.api.enmu.CryptoTypeEnum;

import java.util.Map;

@Model.model(ResourceCrypto.MODEL_MODEL)
@Model.Advanced(name = "resourceCrypto")
@Model(displayName = "加解密模型")
public class ResourceCrypto extends TransientModel {

    public static final String MODEL_MODEL = "resource.ResourceCrypto";

    @Field.Enum
    private CryptoTypeEnum type;

    @Field.String
    @Field(displayName = "公钥")
    private String publicKey;

    @Action
    public ResourceCrypto construct(ResourceCrypto resourceCrypto) throws Exception {
        resourceCrypto.setPublicKey(ResourceConfig.fetchConfigValue(RSAEncryptUtils.PUBLIC_KEY).getValue());
        return resourceCrypto;
    }

    @Action
    public ResourceCrypto genKey(ResourceCrypto resourceCrypto) throws Exception {
        if (CryptoTypeEnum.RSA.equals(resourceCrypto.getType())) {
            Map<String, String> keyMap = RSAEncryptUtils.genKeyPair();
            String rsaPrivateKey = keyMap.get(RSAEncryptUtils.PRIVATE_KEY);
            String rsaPublicKey = keyMap.get(RSAEncryptUtils.PUBLIC_KEY);
            ResourceConfig.pushConfigValue(RSAEncryptUtils.PRIVATE_KEY, rsaPrivateKey);
            ResourceConfig.pushConfigValue(RSAEncryptUtils.PUBLIC_KEY, rsaPublicKey);
        }
        return resourceCrypto;
    }

    /**
     * @param cryptoType 加密类型
     * @param cipher     密文
     * @return
     * @throws Exception
     */
    @Function
    public static String decode(CryptoTypeEnum cryptoType, String cipher) throws Exception {
        if (CryptoTypeEnum.RSA.equals(cryptoType)) {
            String privateKey = ResourceConfig.fetchConfigValue(RSAEncryptUtils.PRIVATE_KEY).getValue();
            if (StringUtils.isNotBlank(privateKey)) {
                return RSAEncryptUtils.decrypt(cipher, privateKey);
            }
        }
        return cipher;
    }
}

package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "cryptotypeenum", displayName = "")
public enum CryptoTypeEnum implements IEnum<String> {
    RSA("RSA", "RSA加密", "RSA加密"),
    AES("AES", "AES加密", "AES加密");

    private String help;

    private String value;

    private String displayName;

    CryptoTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}

package pro.shushi.pamirs.core.common.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = EncryptTypeEnum.dictionary, displayName = "加密类型")
public enum EncryptTypeEnum implements IEnum<String> {

    RSA("RSA", "RSA加密", "RSA加密", 4096),
    AES("AES", "AES加密", "AES加密", 256);

    public static final String dictionary = "resource.EncryptTypeEnum";

    private final String value;
    private final String displayName;
    private final String help;
    private final int initializeSize;

    EncryptTypeEnum(String value, String displayName, String help, int initializeSize) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.initializeSize = initializeSize;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }

    public int getInitializeSize() {
        return initializeSize;
    }
}

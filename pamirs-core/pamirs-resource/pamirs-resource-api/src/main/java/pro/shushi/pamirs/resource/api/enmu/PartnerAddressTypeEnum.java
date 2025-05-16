package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "partnerAddressTypeEnum", displayName = "")
public enum PartnerAddressTypeEnum implements IEnum<String> {

    CORP("CORP", "CORP", "公司地址"),
    DELIVERY("DELIVERY", "DELIVERY", "配送地址"),
    INVOICE("INVOICE", "INVOICE", "发票寄送地址"),
    WAREHOUSE("WAREHOUSE", "WAREHOUSE", "仓库地址"),
    REVERSE("REVERSE", "REVERSE", "售后退货地址"),
    ;

    private String key;

    private String value;

    private String displayName;

    PartnerAddressTypeEnum(String key, String value, String displayName) {
        this.key = key;
        this.value = value;
        this.displayName = displayName;
    }

}

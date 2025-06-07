package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "partnerBankTypeEnum", displayName = "")
public enum PartnerBankTypeEnum implements IEnum<String> {

    NORMAL("NORMAL", "普通银行卡", "普通银行卡"),
    CUSTOMER("CUSTOMER", "自定义类型", "自定义类型"),
    ;

    private String help;

    private String value;

    private String displayName;

    PartnerBankTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}

package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "partnerRelationTypeEnum", displayName = "")
public enum PartnerRelationTypeEnum implements IEnum<String> {

    SALE_CUSTOMER("SALE_CUSTOMER", "销售客户", "销售客户"),
    PURCHASE_CUSTOMER("PURCHASE_CUSTOMER", "采购客户", "采购客户"),
    EMPLOYEE("EMPLOYEE", "所属公司", "所属公司"),
    DEPARTMENT("DEPARTMENT", "所属部门", "所属部门");

    private String help;

    private String value;

    private String displayName;

    PartnerRelationTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}

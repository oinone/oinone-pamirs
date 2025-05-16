package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;

@Base
@Dict(dictionary = "himalaya.trade.BusinessPartnerOptionBitEnum", displayName = "合作伙伴标记")
public class BusinessPartnerOptionBitEnum extends BaseEnum<BusinessPartnerOptionBitEnum, Long> implements BitEnum {

    // 1-20位预留给内部系统使用
    public static final BusinessPartnerOptionBitEnum ORGANIZATION = create("ORGANIZATION", 1L << 0, "组织架构管理标记", "组织架构管理标记");
    public static final BusinessPartnerOptionBitEnum SALE = create("SALE", 1L << 1, "销售标记", "销售标记");
    public static final BusinessPartnerOptionBitEnum SUPPLIER = create("SUPPLIER", 1L << 2, "供货标记", "供货标记");

    // 20位之上其他系统可拓展

}
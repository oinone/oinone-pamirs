package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;

@Base
@Dict(dictionary = "business.BusinessPartnerTypeEnum", displayName = "主体类型")
public class BusinessPartnerTypeEnum extends BaseEnum<BusinessPartnerTypeEnum, String> {

    public static final BusinessPartnerTypeEnum NONE = create("NONE", "NONE", "未知", "未知");
    public static final BusinessPartnerTypeEnum COMPANY = create("COMPANY", "COMPANY", "公司", "公司");
    public static final BusinessPartnerTypeEnum PERSON = create("PERSON", "PERSON", "个人", "个人");

}

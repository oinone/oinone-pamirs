package pro.shushi.pamirs.resource.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Dict(dictionary = "addresstypeenum", displayName = "地址类型枚举")
public enum AddressTypeEnum implements IEnum<String> {
    Country("country", "国家", "国家"),
    Province("province", "省", "省"),
    City("city", "市", "市"),
    District("district", "区", "区"),
    Street("street", "街道", "街道");

    private String help;

    private String value;

    private String displayName;

    AddressTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


}

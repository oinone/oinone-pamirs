package pro.shushi.pamirs.sys.setting.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * TenantDomainEnum
 *
 * @author yakir on 2022/09/15 13:58.
 */
@Dict(dictionary = TenantDomainEnum.dict, displayName = "租户域名访问方式")
public enum TenantDomainEnum implements IEnum<String> {

    L1_DOMAIN("L1_DOMAIN", "顶级路径", "https://www.oinone.top/顶级路径/"),
    L3_DOMAIN("L3_DOMAIN", "三级域名", "https://三级域名.oinone.top"),

    ;

    public static final String dict = "sysSetting.TenantDomainEnum";


    private final String value;
    private final String displayName;
    private final String help;

    TenantDomainEnum(String value, String displayName, String help) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
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
}

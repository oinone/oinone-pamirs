package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * CompanyInitEnum
 *
 * @author yakir on 2022/11/14 19:11.
 */
@Dict(dictionary = CompanyInitEnum.dictionary, displayName = "租户初始化场景")
public enum CompanyInitEnum implements IEnum<String> {

    INIT_TENANT("INIT_TENANT", "初始化租户", "初始化租户"),
    INIT_APPS("INIT_APPS", "初始化应用", "初始化应用"),
    INIT_TENANT_APPS("INIT_TENANT_APPS", "初始化租户和应用", "初始化租户和应用"),
    ;

    public static final String dictionary = "business.CompanyInitEnum";

    private final String value;
    private final String displayName;
    private final String help;

    CompanyInitEnum(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
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

    public static CompanyInitEnum fetchByValue(String value) {
        for (CompanyInitEnum welcomeChannelEnum : CompanyInitEnum.values()) {
            if (welcomeChannelEnum.getValue().equals(value)) {
                return welcomeChannelEnum;
            }
        }
        return null;
    }

}
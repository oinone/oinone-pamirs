package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * CompanyProgressEnum
 *
 * @author yakir on 2022/11/17 14:05.
 */
@Dict(dictionary = CompanyProgressEnum.dictionary, displayName = "租户初始化进程状态")
public enum CompanyProgressEnum implements IEnum<String> {

    CREATED("CREATED", "创建完成", "创建完成"),
    SET_DOMAIN("SET_DOMAIN", "设置域名", "设置域名"),
    CHOOSE_APP("CHOOSE_APP", "选择安装应用", "选择安装应用"),
    INSTALL_APP("INSTALL_APP", "安装应用", "安装应用"),
    DEPLOYED("DEPLOYED", "部署成功", "部署成功"),

    ;

    public static final String dictionary = "business.CompanyProgressEnum";

    private final String value;
    private final String displayName;
    private final String help;

    CompanyProgressEnum(String value, String displayName, String help) {
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

    public static CompanyInitEnum fetchByValue(String value) {
        for (CompanyInitEnum welcomeChannelEnum : CompanyInitEnum.values()) {
            if (welcomeChannelEnum.getValue().equals(value)) {
                return welcomeChannelEnum;
            }
        }
        return null;
    }
}

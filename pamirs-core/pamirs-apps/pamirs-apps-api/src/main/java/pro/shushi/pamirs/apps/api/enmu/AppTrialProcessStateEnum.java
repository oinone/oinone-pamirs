package pro.shushi.pamirs.apps.api.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = AppTrialProcessStateEnum.dictionary, displayName = "试用节点安装状态")
public enum AppTrialProcessStateEnum implements IEnum<String> {

    INSTALLING("installing", "正在安装", "正在安装"),
    INSTALLED("installed", "安装完成", "安装完成"),
    INACTIVE("inactive", "试用到期", "试用到期"),
    ;

    public static final String dictionary = "app.AppTrialProcessStateEnum";

    private String value;
    private String displayName;
    private String help;

    AppTrialProcessStateEnum(String value, String displayName, String help) {
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

    public static AppTrialProcessStateEnum fetchByValue(String value) {
        for (AppTrialProcessStateEnum welcomeChannelEnum : AppTrialProcessStateEnum.values()) {
            if (welcomeChannelEnum.getValue().equals(value)) {
                return welcomeChannelEnum;
            }
        }
        return null;
    }

}

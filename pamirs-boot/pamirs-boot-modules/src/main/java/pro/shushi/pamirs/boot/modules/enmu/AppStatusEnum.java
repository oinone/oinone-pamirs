package pro.shushi.pamirs.boot.modules.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * AppStatusEnum
 *
 * @author yakir on 2022/09/02 16:26.
 */
@Base
@Dict(dictionary = AppStatusEnum.dictionary, displayName = "应用状态筛选")
public enum AppStatusEnum implements IEnum<String> {

    ALL("ALL", "全部", "全部"),
    INSTALLED("INSTALLED", "已安装", "已安装"),
    UNINSTALL("UNINSTALL", "未安装", "未安装"),
    ;

    public static final String dictionary = "apps.AppStatusEnum";

    private final String value;
    private final String displayName;
    private final String help;

    AppStatusEnum(String value, String displayName, String help) {
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
}

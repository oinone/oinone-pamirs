package pro.shushi.pamirs.boot.modules.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * AppLikeEnum
 *
 * @author yakir on 2022/09/02 16:26.
 */
@Base
@Dict(dictionary = AppLikeEnum.dictionary, displayName = "星标应用筛选")
public enum AppLikeEnum implements IEnum<String> {

    ALL("ALL", "全部", "全部"),
    LIKE("LIKE", "已收藏", "已收藏"),
    NOT_LIKE("NOT_LIKE", "未收藏", "未收藏"),
    ;

    public static final String dictionary = "apps.AppLikeEnum";

    private final String value;
    private final String displayName;
    private final String help;

    AppLikeEnum(String value, String displayName, String help) {
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

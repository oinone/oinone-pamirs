package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnAuthStatus
 *
 * @author yakir on 2023/03/29 15:25.
 */
@Base
@Dict(dictionary = ConnAuthStatus.dictionary, displayName = "认证状态", summary = "认证状态")
public enum ConnAuthStatus implements IEnum<String> {

    AUTHED("AUTHED", "已认证", "已认证"),
    UNAUTH("UNAUTH", "未认证", "添加认证"),
    ;

    public static final String dictionary = "designer.ConnAuthStatus";

    private final String value;
    private final String displayName;
    private final String help;

    ConnAuthStatus(String value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String help() {
        return help;
    }
}

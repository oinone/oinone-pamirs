package pro.shushi.pamirs.eip.api.enmu.connector;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * ConnDBType
 *
 * @author yakir on 2023/03/29 16:07.
 */
@Base
@Dict(dictionary = ConnApiType.dictionary, displayName = "API类型", summary = "API类型")
public enum ConnApiType implements IEnum<String> {

    Restful("Restful", "Rustful", "Rustful"),
    WebService("WebService", "WebService", "WebService"),
//    DB("DB", "DB", "DB"),

    ;

    public static final String dictionary = "designer.ConnApiType";

    private final String value;
    private final String displayName;
    private final String help;

    ConnApiType(String value, String displayName, String help) {
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

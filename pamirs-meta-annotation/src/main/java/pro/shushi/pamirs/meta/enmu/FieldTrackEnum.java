package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 字段追踪枚举
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Dict(dictionary = "base.FieldTrack", displayName = "字段跟踪类型")
public enum FieldTrackEnum implements IEnum<String> {

    NON("no_track", "不追踪", "不追踪"),
    ALWAYS("always", "永远", "永远"),
    ON_CHANGE("on_change", "变更", "变更");

    private String help;
    private String value;
    private String displayName;

    FieldTrackEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }

}

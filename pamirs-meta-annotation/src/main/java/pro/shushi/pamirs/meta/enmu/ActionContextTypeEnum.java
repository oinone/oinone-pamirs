package pro.shushi.pamirs.meta.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "base.ActionContextType", displayName = "动作上下文")
public enum ActionContextTypeEnum implements IEnum<String> {

    SINGLE("SINGLE", "单行", "单行"),
    BATCH("BATCH", "多行", "多行"),
    SINGLE_AND_BATCH("SINGLE_AND_BATCH", "单行和多行", "单行和多行"),
    CONTEXT_FREE("CONTEXT_FREE", "上下文无关", "上下文无关"),
    ;

    private String value;

    private String displayName;

    private String help;

    ActionContextTypeEnum(String  value, String displayName, String help) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
    }

}

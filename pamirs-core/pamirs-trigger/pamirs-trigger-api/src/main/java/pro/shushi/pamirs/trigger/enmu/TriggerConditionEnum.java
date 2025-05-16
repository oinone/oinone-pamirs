package pro.shushi.pamirs.trigger.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = TriggerConditionEnum.DICTIONARY, displayName = "触发场景", summary = "触发场景")
public enum TriggerConditionEnum implements IEnum<String> {

    /**
     * 创建时
     */
    ON_CREATE("ON_CREATE", "创建时", "创建时", "onCreate", 0),

    /**
     * 更新时
     */
    ON_UPDATE("ON_UPDATE", "更新时", "更新时", "onUpdate", 1),

    /**
     * 删除时
     */
    ON_DELETE("ON_DELETE", "删除时", "删除时", "onDelete", -1);

    public static final String DICTIONARY = "trigger.TriggerConditionEnum";

    private final String value;
    private final String displayName;
    private final String help;

    private final int intValue;
    private final String defaultMethodName;

    TriggerConditionEnum(String value, String displayName, String help, String defaultMethodName, int intValue) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.defaultMethodName = defaultMethodName;
        this.intValue = intValue;
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

    public String getDefaultMethodName() {
        return defaultMethodName;
    }

    public int intValue() {
        return intValue;
    }

    //    public boolean contains(Collection<TriggerConditionEnum> collection) {
//        if (CollectionUtils.isEmpty(collection)) {
//            return Boolean.FALSE;
//        }
//        for (TriggerConditionEnum item : collection) {
//            if (item.getValue().equals(this.getValue())) {
//                return Boolean.TRUE;
//            }
//        }
//        return Boolean.FALSE;
//    }
}

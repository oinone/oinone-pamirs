package pro.shushi.pamirs.eip.api.behavior.enmu;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.directive.Directive;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.List;

@Dict(dictionary = SynchronizationStatusEnum.DICTIONARY, displayName = "同步状态")
public enum SynchronizationStatusEnum implements IEnum<String>, Directive {

    NONE("NONE", "无需同步", "无需同步", 1),
    PENDING("PENDING", "未同步", "未同步", 2),
    SUCCESS("SUCCESS", "已同步", "已同步", 2 << 1),
    EXCEPTION("EXCEPTION", "同步异常", "同步异常", 2 << 2),
    PART_SUCCESS("PART_SUCCESS", "部分同步", "部分同步", 2 << 3),
    ;

    public static final String DICTIONARY = "pamirs.eip.SynchronizationStatusEnum";

    private final String value;
    private final String displayName;
    private final String help;

    private final int intValue;

    SynchronizationStatusEnum(String value, String displayName, String help, int intValue) {
        this.value = value;
        this.displayName = displayName;
        this.help = help;
        this.intValue = intValue;
    }

    public String getValue() {
        return this.value;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getHelp() {
        return this.help;
    }

    @Override
    public int intValue() {
        return intValue;
    }

    public static boolean isSuccess(SynchronizationStatusEnum synchronizationStatus) {
        return SUCCESS.equals(synchronizationStatus) || NONE.equals(synchronizationStatus);
    }

    /**
     * 多个状态值,聚合成1个
     *
     * @param statusList 状态列表
     * @return 最终枚举状态
     */
    public static SynchronizationStatusEnum aggCalculate(List<SynchronizationStatusEnum> statusList) {
        if (CollectionUtils.isEmpty(statusList)) {
            return null;
        }
        int bitValue = DirectiveHelper.enable(0, statusList);
        if (DirectiveHelper.isBitValue(bitValue)) {
            return statusList.get(0);
        }
        boolean hasSuccess = DirectiveHelper.isEnabled(bitValue, SUCCESS);
        boolean hasException = DirectiveHelper.isEnabled(bitValue, EXCEPTION);
        boolean hasPending = DirectiveHelper.isEnabled(bitValue, PENDING);
        if (hasSuccess) {
            return hasException || hasPending ? PART_SUCCESS : SUCCESS;
        } else if (hasException) {
            return EXCEPTION;
        } else {
            return PENDING;
        }
    }
}

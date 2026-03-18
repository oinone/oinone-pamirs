package pro.shushi.pamirs.boot.standard.checker.helper;

import pro.shushi.pamirs.boot.standard.checker.constants.EnvironmentCheckConstants;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

/**
 * 布尔值检查
 *
 * @author Adamancy Zhang at 12:35 on 2024-10-17
 */
public class BooleanChecker implements EnvironmentKey.Checker {

    /**
     * 不可变
     */
    private final boolean immutable;

    /**
     * 当值为指定值时打印提示信息
     */
    private final boolean predict;

    public BooleanChecker(boolean immutable, boolean predict) {
        this.immutable = immutable;
        this.predict = predict;
    }

    @Override
    public PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        String oldValue = historyEnvironment.getValue();
        String newValue = currentEnvironment.getValue();
        if (immutable && !oldValue.equals(newValue)) {
            context.addError(currentEnvironment, EnvironmentCheckConstants.getImmutableTip() + historyEnvironment.getValue());
        }
        if (newValue.equals(String.valueOf(predict))) {
            context.addContext(currentEnvironment);
        }
        return currentEnvironment;
    }
}

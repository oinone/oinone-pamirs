package pro.shushi.pamirs.boot.standard.checker.helper;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.standard.checker.constants.EnvironmentCheckConstants;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

/**
 * 默认检查
 *
 * @author Adamancy Zhang at 17:07 on 2024-12-02
 */
public class DefaultChecker implements EnvironmentKey.Checker {

    public static final EnvironmentKey.Checker INSTANCE = new DefaultChecker();

    @Override
    public PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        if (context.isImmutableEnvironment()) {
            addContext(context, currentEnvironment, historyEnvironment);
        } else {
            addUpdate(context, currentEnvironment, historyEnvironment);
        }
        return currentEnvironment;
    }

    protected void addContext(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        String oldValue = historyEnvironment.getValue();
        String newValue = currentEnvironment.getValue();
        EnvironmentKey key = context.getKey(currentEnvironment);
        switch (key.getLevel()) {
            case IMMUTABLE:
                if (newValue == null || !newValue.equals(oldValue)) {
                    // FIXME: zbh 20250908 兼容旧版环境检查未处理环境变量的问题
                    if (oldValue != null && oldValue.startsWith("${") && oldValue.endsWith("}")) {
                        break;
                    }
                    addError(context, currentEnvironment, getErrorMessage(key.getMessage(), EnvironmentCheckConstants.IMMUTABLE_TIP + oldValue));
                }
                break;
            case ADD:
            case ADD_OR_DELETE:
                if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                    // FIXME: zbh 20250908 兼容旧版环境检查未处理环境变量的问题
                    if (oldValue.startsWith("${") && oldValue.endsWith("}")) {
                        break;
                    }
                    addError(context, currentEnvironment, getErrorMessage(key.getMessage(), EnvironmentCheckConstants.IMMUTABLE_TIP + oldValue));
                }
                break;
            case ERROR:
                addError(context, currentEnvironment);
                break;
            case WARNING:
                if (newValue == null || !newValue.equals(oldValue)) {
                    addWarning(context, currentEnvironment);
                }
                break;
            case DEPRECATED:
                addDeprecated(context, currentEnvironment);
                break;
        }
    }

    protected void addUpdate(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        String oldValue = historyEnvironment.getValue();
        String newValue = currentEnvironment.getValue();
        if (newValue == null || !newValue.equals(oldValue)) {
            context.addUpdate(currentEnvironment);
        }
    }

    protected void addUpdate(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addUpdate(currentEnvironment);
    }

    protected void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addError(currentEnvironment);
    }

    protected void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addError(currentEnvironment, message);
    }

    protected void addWarning(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addWarning(currentEnvironment);
    }

    protected void addWarning(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addWarning(currentEnvironment, message);
    }

    protected void addDeprecated(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addDeprecated(currentEnvironment);
    }

    protected void addDeprecated(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addDeprecated(currentEnvironment, message);
    }

    protected String getErrorMessage(String message, String defaultMessage) {
        if (StringUtils.isBlank(message)) {
            return defaultMessage;
        }
        return message;
    }
}

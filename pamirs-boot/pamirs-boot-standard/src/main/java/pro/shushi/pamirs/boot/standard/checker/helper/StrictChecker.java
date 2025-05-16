package pro.shushi.pamirs.boot.standard.checker.helper;

import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

/**
 * 严格检查（默认）
 *
 * @author Adamancy Zhang at 17:07 on 2024-12-02
 */
public class StrictChecker extends DefaultChecker implements EnvironmentKey.Checker {

    private final boolean strict;

    public StrictChecker(boolean strict) {
        this.strict = strict;
    }

    @Override
    protected void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        if (strict) {
            context.addError(currentEnvironment);
        } else {
            context.addWarning(currentEnvironment);
        }
    }

    @Override
    protected void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        if (strict) {
            context.addError(currentEnvironment, message);
        } else {
            context.addWarning(currentEnvironment, message);
        }
    }
}

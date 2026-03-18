package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.constants.EnvironmentCheckConstants;
import pro.shushi.pamirs.boot.standard.checker.helper.BooleanChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;

/**
 * 网关环境检查
 *
 * @author Adamancy Zhang at 12:24 on 2024-10-17
 */
@Component
public class PamirsGatewayEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    @Override
    protected EnvironmentKeySet warningKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.WARNING,
                EnvironmentKey.warning("pamirs.framework.gateway.statistics", Boolean.FALSE.toString(), EnvironmentCheckConstants.getProductEnvironmentClosedTip(), new BooleanChecker(false, true)),
                EnvironmentKey.warning("pamirs.framework.gateway.show-doc", Boolean.FALSE.toString(), EnvironmentCheckConstants.getProductEnvironmentClosedTip(), new BooleanChecker(false, true)),
                EnvironmentKey.warning("pamirs.framework.gateway.async", Boolean.FALSE.toString(), EnvironmentCheckConstants.getProductEnvironmentEnabledTip(), new BooleanChecker(false, false))
        );
    }
}

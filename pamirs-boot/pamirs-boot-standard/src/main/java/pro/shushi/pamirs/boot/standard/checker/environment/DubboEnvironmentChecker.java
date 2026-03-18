package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.StrictChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

/**
 * Dubbo环境检查
 *
 * @author Adamancy Zhang at 13:26 on 2024-10-25
 */
@Component
public class DubboEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    private static final String DUBBO_SERIALIZATION_ERROR = "DubboEnvironmentChecker.DUBBO_SERIALIZATION_ERROR";

    private static final HoldKeeper<Boolean> isUsingDistributionFaas = new HoldKeeper<>();

    private static boolean isUsingDistributionFaas() {
        return isUsingDistributionFaas.supply(() -> {
            try {
                Class.forName("pro.shushi.pamirs.distribution.faas.serialize.KryoSerialization");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
    }

    @Override
    protected EnvironmentKeySet propertyKeys() {
        if (!isUsingDistributionFaas()) {
            return EnvironmentKeySet.emptySet();
        }
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                EnvironmentKey.immutable("dubbo.registry.address", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                "dubbo.protocol.name",
                EnvironmentKey.immutable("dubbo.protocol.serialization", new SerializationChecker())
        );
    }

    private static class SerializationChecker implements EnvironmentKey.Checker {

        @Override
        public PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
            return checkNewEnvironment(context, currentEnvironment);
        }

        @Override
        public PlatformEnvironment checkNewEnvironment(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
            if (!isUsingDistributionFaas()) {
                return currentEnvironment;
            }
            String newValue = currentEnvironment.getValue();
            if (!NamespaceConstants.pamirs.equals(newValue)) {
                context.addError(currentEnvironment, I18nUtils.getMessage(DUBBO_SERIALIZATION_ERROR));
            }
            return currentEnvironment;
        }

        @Override
        public PlatformEnvironment checkDeleteEnvironment(EnvironmentCheckContext context, PlatformEnvironment historyEnvironment) {
            if (!isUsingDistributionFaas()) {
                return null;
            }
            context.addError(historyEnvironment, I18nUtils.getMessage(DUBBO_SERIALIZATION_ERROR));
            return null;
        }
    }
}

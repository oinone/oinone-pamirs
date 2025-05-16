package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.StrictChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;

/**
 * PamirsEvent环境检查
 *
 * @author Adamancy Zhang at 16:21 on 2024-10-11
 */
@Component
public class PamirsEventEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    @Override
    protected EnvironmentKeySet propertyKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                "pamirs.event.topic-prefix",
                newEnvironmentKeySet(EnvironmentKey.Level.ADD_OR_DELETE,
                        EnvironmentKey.addOrDelete("spring.rocketmq.name-server", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                        EnvironmentKey.addOrDelete("spring.rabbitmq.host", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                        EnvironmentKey.addOrDelete("spring.rabbitmq.port", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                        EnvironmentKey.addOrDelete("spring.kafka.bootstrap-servers", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                        "pamirs.event.notify-map.system",
                        "pamirs.event.notify-map.biz",
                        "pamirs.event.notify-map.logger"
                )
        );
    }

    @Override
    protected EnvironmentKeySet deprecatedKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.DEPRECATED,
                "pamirs.event.rocket-mq.namesrv-addr"
        );
    }
}

package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.StrictChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;

/**
 * Redis环境检查
 *
 * @author Adamancy Zhang at 16:21 on 2024-10-11
 */
@Component
public class RedisEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    @Override
    protected EnvironmentKeySet propertyKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                EnvironmentKey.immutable("spring.redis.host", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                EnvironmentKey.immutable("spring.redis.port", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                "spring.redis.prefix",
                "spring.redis.database"
        );
    }
}

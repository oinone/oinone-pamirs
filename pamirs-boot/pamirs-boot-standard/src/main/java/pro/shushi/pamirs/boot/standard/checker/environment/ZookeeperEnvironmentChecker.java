package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.checker.helper.StrictChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;

/**
 * Zookeeper环境检查
 *
 * @author Adamancy Zhang at 18:29 on 2024-10-12
 */
@Component
public class ZookeeperEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    @Override
    protected EnvironmentKeySet propertyKeys() {
        return newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                EnvironmentKey.immutable("pamirs.zookeeper.zk-connect-string", new StrictChecker(EnvironmentProtectedConfig.isStrict())),
                "pamirs.zookeeper.root-path"
        );
    }
}

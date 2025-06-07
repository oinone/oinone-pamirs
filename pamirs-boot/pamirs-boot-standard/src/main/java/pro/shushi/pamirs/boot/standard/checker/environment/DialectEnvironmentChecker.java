package pro.shushi.pamirs.boot.standard.checker.environment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKeySet;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;

/**
 * 方言环境检查
 *
 * @author Adamancy Zhang at 16:11 on 2024-10-14
 */
@Component
public class DialectEnvironmentChecker extends AbstractPlatformEnvironmentChecker implements PlatformEnvironmentChecker {

    private static final String PAMIRS_DIALECT_KEY_PREFIX_FORMAT = ConfigureConstants.DIALECT_DATASOURCE_PREFIX + "[%s].";

    @Autowired
    private DataSourceConfiguration dataSourceConfiguration;

    @Override
    protected EnvironmentKeySet propertyKeys() {
        EnvironmentKeySet keys = newEnvironmentKeySet(EnvironmentKey.Level.IMMUTABLE,
                "pamirs.event.schedule.dialect.type",
                "pamirs.event.schedule.dialect.version",
                "pamirs.event.schedule.dialect.major-version"
        );
        for (String dsKey : dataSourceConfiguration.keySet()) {
            String keyPrefix = String.format(PAMIRS_DIALECT_KEY_PREFIX_FORMAT, dsKey);
            keys.add(EnvironmentKey.immutable(keyPrefix + "type"));
            keys.add(EnvironmentKey.immutable(keyPrefix + "version"));
            keys.add(EnvironmentKey.immutable(keyPrefix + "major-version"));
        }
        return keys;
    }
}

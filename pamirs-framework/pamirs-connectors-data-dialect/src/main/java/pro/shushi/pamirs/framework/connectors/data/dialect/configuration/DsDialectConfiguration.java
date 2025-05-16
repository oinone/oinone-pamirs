package pro.shushi.pamirs.framework.connectors.data.dialect.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.framework.connectors.data.dialect.factory.DialectVersion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源方言配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.DIALECT_DATASOURCE_PREFIX)
@RefreshScope
public class DsDialectConfiguration extends ConcurrentHashMap<String, Map<String, String>> {

    private static final long serialVersionUID = 7570617456936054529L;

    public DialectVersion dialectVersion(String dsKey) {
        Map<String, String> conf = get(dsKey);
        if (null == conf) {
            return new DialectVersion().setType(DataProductVersion.DEFAULT_PRODUCT)
                    .setMajorVersion(DataProductVersion.DEFAULT_MYSQL_MAJOR_VERSION)
                    .setVersion(DataProductVersion.DEFAULT_MYSQL_VERSION);
        }
        String type = Optional.ofNullable(conf.get(DataProductVersion.KEY_TYPE)).orElse(DataProductVersion.DEFAULT_PRODUCT);
        String originMajorVersion = conf.get(DataProductVersion.KEY_MAJOR_VERSION);
        String majorVersion = Optional.ofNullable(originMajorVersion).orElse(DataProductVersion.DEFAULT_MYSQL_MAJOR_VERSION);
        String version = Optional.ofNullable(conf.get(DataProductVersion.KEY_VERSION))
                .orElse(Optional.ofNullable(originMajorVersion).orElse(DataProductVersion.DEFAULT_MYSQL_VERSION));
        return new DialectVersion().setType(type).setMajorVersion(majorVersion).setVersion(version);
    }

}

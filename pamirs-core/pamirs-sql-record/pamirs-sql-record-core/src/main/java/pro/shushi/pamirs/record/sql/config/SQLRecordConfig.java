package pro.shushi.pamirs.record.sql.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * SQLRecordConfig
 *
 * @author yakir on 2023/06/29 14:56.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pamirs.record.sql")
public class SQLRecordConfig {

    private String store;

    private boolean lock = true;
}

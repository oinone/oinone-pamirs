package pro.shushi.pamirs.eip.jdbc.manager;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.alibaba.druid.pool.vendor.PGValidConnectionChecker;
import com.alibaba.druid.util.Utils;
import com.google.common.collect.Sets;
import org.apache.camel.CamelContext;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.eip.api.constant.EipSystemDataSourceType;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.jdbc.config.EipJdbcProperties;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * JDBC数据源管理器
 *
 * @author Adamancy Zhang at 14:19 on 2024-06-05
 */
@Slf4j
public class EipDataSourceManager {

    private static final Map<String, DataSource> DATA_SOURCES = new ConcurrentHashMap<>();

    private static final String DATASOURCE_ID_PREFIX = "__eip_datasource_";

    private EipDataSourceManager() {
        //reject create object
    }

    public static DataSource buildSimpleDataSource(String jdbcUrl, String driverClassName, String username, String password) {
        return buildSimpleDataSource(jdbcUrl, driverClassName, username, password, null);
    }

    public static DataSource buildSimpleDataSource(String jdbcUrl, String driverClassName, String username, String password, String dbType) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(200);
        dataSource.setMinIdle(5);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setBreakAfterAcquireFailure(true);
        // DruidDataSource#initValidConnectionChecker()
        if (dbType == null) {
            if (DataSourceProtocolEnum.DM.value().startsWith(jdbcUrl)) {
                try {
                    dataSource.setValidConnectionCheckerClassName(OracleValidConnectionChecker.class.getName());
                } catch (Exception e) {
                    log.error("Set valid connection checker error.", e);
                }
            }
        } else {
            try {
                if (EipSystemDataSourceType.mysql().getCode().equals(dbType)) {
                    dataSource.setValidConnectionCheckerClassName(MySqlValidConnectionChecker.class.getName());
                } else if (EipSystemDataSourceType.oracle().getCode().equals(dbType)) {
                    dataSource.setValidConnectionCheckerClassName(OracleValidConnectionChecker.class.getName());
                } else if (EipSystemDataSourceType.mssql().getCode().equals(dbType)) {
                    dataSource.setValidConnectionCheckerClassName(MSSQLValidConnectionChecker.class.getName());
                } else if (EipSystemDataSourceType.pgsql().getCode().equals(dbType)) {
                    dataSource.setValidConnectionCheckerClassName(PGValidConnectionChecker.class.getName());
                }
            } catch (Exception e) {
                log.error("Set valid connection checker error.", e);
            }
        }
        EipJdbcProperties eipJdbcProperties = BeanDefinitionUtils.getBean(EipJdbcProperties.class);
        if (eipJdbcProperties != null) {
            Map<String, String> properties = eipJdbcProperties.getDataSource();
            if (!properties.isEmpty()) {
                DruidSafeProperties safeProperties = new DruidSafeProperties(properties);
                dataSource.configFromPropety(safeProperties);
                extraConfigFromProperty(dataSource, safeProperties);
            }
        }
        return dataSource;
    }

    private static void extraConfigFromProperty(DruidDataSource dataSource, DruidSafeProperties safeProperties) {
        {
            Integer connectionErrorRetryAttempts = Utils.getInteger(safeProperties, "druid.connectionErrorRetryAttempts");
            if (connectionErrorRetryAttempts != null) {
                dataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
            }
        }
        {
            Boolean breakAfterAcquireFailure = Utils.getBoolean(safeProperties, "druid.breakAfterAcquireFailure");
            if (breakAfterAcquireFailure != null) {
                dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
            }
        }
    }

    public static String generatorId(String dsKey) {
        return DATASOURCE_ID_PREFIX + dsKey;
    }

    public static String resolveDsKey(String id) {
        return id.substring(DATASOURCE_ID_PREFIX.length());
    }

    public static boolean register(String dsKey, Supplier<DataSource> dataSourceSupplier) {
        Holder<Boolean> resultHolder = new Holder<>(false);
        String id = generatorId(dsKey);
        DATA_SOURCES.computeIfAbsent(id, (key) -> {
            DataSource dataSource = register0(key, dataSourceSupplier);
            resultHolder.set(true);
            return dataSource;
        });
        return resultHolder.get();
    }

    private static DataSource register0(String key, Supplier<DataSource> dataSourceSupplier) {
        CamelContext context = EipCamelContext.getContext().getCamelContext();
        log.info("register {} data source", key);
        DataSource dataSource = new EipDynamicDataSource(dataSourceSupplier.get());
        context.getRegistry().bind(key, dataSource);
        return dataSource;
    }

    public static Set<String> keySet() {
        return DATA_SOURCES.keySet();
    }

    public static DataSource get(String dsKey) {
        String id = generatorId(dsKey);
        return DATA_SOURCES.get(id);
    }

    public static boolean close(String dsKey) {
        String id = generatorId(dsKey);
        DataSource dataSource = DATA_SOURCES.get(id);
        if (dataSource == null) {
            log.info("Not found {} data source", id);
            return true;
        }
        return close(id, dataSource);
    }

    public static boolean refresh(String dsKey, Supplier<DataSource> dataSourceSupplier) {
        Holder<Boolean> resultHolder = new Holder<>(false);
        String id = generatorId(dsKey);
        DATA_SOURCES.compute(id, (key, value) -> {
            log.info("refresh {} data source", key);
            close(key, value);
            DataSource dataSource = refresh0(key, value, dataSourceSupplier);
            resultHolder.set(true);
            return dataSource;
        });
        return resultHolder.get();
    }

    private static DataSource refresh0(String key, DataSource value, Supplier<DataSource> dataSourceSupplier) {
        if (value == null) {
            return register0(key, dataSourceSupplier);
        } else if (value instanceof EipDynamicDataSource) {
            ((EipDynamicDataSource) value).setDataSource(dataSourceSupplier.get());
            return value;
        }
        log.error("unsupported refresh data source. key: {}", key);
        return value;
    }

    private static boolean close(String id, DataSource dataSource) {
        if (dataSource instanceof EipDynamicDataSource) {
            dataSource = ((EipDynamicDataSource) dataSource).getDataSource();
        }
        if (dataSource instanceof DruidDataSource) {
            ((DruidDataSource) dataSource).setEnable(false);
        } else if (dataSource instanceof Closeable) {
            log.info("closing {} data source", id);
            try {
                ((Closeable) dataSource).close();
                log.info("closed {} data source", id);
            } catch (IOException e) {
                log.error("close {} data source error.", id, e);
                return false;
            }
        }
        return true;
    }

    private static class DruidSafeProperties extends Properties {

        private static final long serialVersionUID = 3738563489065989194L;

        private static final String PREFIX = "druid.";

        private static final Set<String> IGNORED_KEYS = Sets.newHashSet(
                "druid.url",
                "druid.driverClassName",
                "druid.username",
                "druid.password"
        );

        private final Map<String, String> origin;

        public DruidSafeProperties(Map<String, String> origin) {
            this.origin = origin;
        }

        @Override
        public String getProperty(String key) {
            if (IGNORED_KEYS.contains(key)) {
                return null;
            }
            key = key.substring(PREFIX.length());
            return origin.get(key);
        }
    }
}

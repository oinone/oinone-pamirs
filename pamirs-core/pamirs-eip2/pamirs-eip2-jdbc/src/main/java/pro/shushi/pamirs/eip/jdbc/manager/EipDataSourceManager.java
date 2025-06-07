package pro.shushi.pamirs.eip.jdbc.manager;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import org.apache.camel.CamelContext;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.DataSourceProtocolEnum;

import javax.sql.DataSource;
import java.util.Map;
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
        // DruidDataSource#initValidConnectionChecker()
        if (DataSourceProtocolEnum.DM.value().startsWith(jdbcUrl)) {
            try {
                dataSource.setValidConnectionCheckerClassName(OracleValidConnectionChecker.class.getName());
            } catch (Exception e) {
                log.error("Set valid connection checker error.", e);
            }
        }
        return dataSource;
    }

    public static String generatorId(String dsKey) {
        return DATASOURCE_ID_PREFIX + dsKey;
    }

    public static boolean register(String dsKey, Supplier<DataSource> dataSourceSupplier) {
        CamelContext context = EipCamelContext.getContext().getCamelContext();
        Holder<Boolean> resultHolder = new Holder<>(false);
        String id = generatorId(dsKey);
        DATA_SOURCES.computeIfAbsent(id, (key) -> {
            DataSource dataSource = dataSourceSupplier.get();
            context.getRegistry().bind(key, dataSource);
            resultHolder.set(true);
            return dataSource;
        });
        return resultHolder.get();
    }

    public static boolean refresh(String dsKey, Supplier<DataSource> dataSourceSupplier) {
        CamelContext context = EipCamelContext.getContext().getCamelContext();
        Holder<Boolean> resultHolder = new Holder<>(false);
        String id = generatorId(dsKey);
        DataSource dataSource = dataSourceSupplier.get();
        context.getRegistry().bind(id, dataSource);
        resultHolder.set(true);
        DATA_SOURCES.put(id, dataSource);
        return resultHolder.get();
    }
}

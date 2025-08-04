package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.yaml.swapper.YamlRuleConfigurationSwapperEngine;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.configure.datasource.DataSourceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.persistence.PamirsPersistenceConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.ShardingRuleConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingSpecificConfiguration;
import pro.shushi.pamirs.framework.connectors.data.constant.SystemBeanConstants;
import pro.shushi.pamirs.framework.connectors.data.datasource.ddl.DdlManager;
import pro.shushi.pamirs.framework.connectors.data.datasource.event.RefreshContext;
import pro.shushi.pamirs.framework.connectors.data.datasource.event.RefreshEventHandler;
import pro.shushi.pamirs.framework.connectors.data.datasource.factory.PamirsDataSourceFactory;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_DATASOURCE_CONFIG_IS_NOT_EXISTS_ERROR;


/**
 * 数据源自动更新管理器
 * <p>
 * 2020/1/10 4:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
@DependsOn({BeanDefinitionUtils.beanName})
public class DataSourceAutoRefreshManager {

    public static final String beanName = "dataSourceAutoRefreshManager";

    @Resource
    private DdlManager ddlManager;

    @Resource
    private DataSourceHolder dataSourceHolder;

    private static final YamlRuleConfigurationSwapperEngine SWAPPER_ENGINE = new YamlRuleConfigurationSwapperEngine();

    private final static List<RefreshEventHandler> handlers = new ArrayList<>();

    @SuppressWarnings("unused")
    @PostConstruct
    void dsInit() {
        // 注册数据源处理器
        Map<String, RefreshEventHandler> refreshEventHandlerMap = BeanDefinitionUtils.getBeansOfType(RefreshEventHandler.class);
        handlers.addAll(Objects.requireNonNull(refreshEventHandlerMap).values());

        log.info("dataSource init.");
        // 注册数据源
        DataSourceConfiguration dataSourceConfiguration = getDataSourceConfiguration();
        for (String dsKey : dataSourceConfiguration.keySet()) {
            refreshDataSource(dsKey);
        }
    }

    @EventListener
    @Order(10)
    public void shardingInit(ApplicationStartedEvent event) throws Exception {
        log.info("sharding dataSource trigger with application started." + event.getTimestamp());
        // 注册sharding数据源
        ShardingRuleConfiguration shardingConfiguration = getShardingConfiguration();
        for (String dsKey : shardingConfiguration.keySet()) {
            refreshShardingDataSource(dsKey);
        }
    }

    @EventListener
    @Order(0)
    public void refreshDataSourceMap(EnvironmentChangeEvent event) {
        Set<String> changeKeys = event.getKeys();
        RefreshContext context = new RefreshContext();
        for (RefreshEventHandler handler : handlers) {
            for (String key : changeKeys) {
                if (!handler.needHandle(key)) {
                    continue;
                }
                handler.handle(context, key);
            }
        }

        for (String refreshConfName : context.getRefreshConfSet()) {
            refresh(refreshConfName);
        }

        for (String key : context.getRefreshDsSet()) {
            refreshDataSource(key);
        }

        for (String key : context.getRefreshAllSet()) {
            try {
                refreshShardingDataSource(key);
            } catch (Exception e) {
                log.error(CharacterConstants.LOG_PLACEHOLDER, DataExpEnumerate.BASE_SHARDING_CONFIG_ERROR.msg(), e);
            }
        }
    }

    /**
     * 刷新普通数据源
     *
     * @param dsKey 数据源key
     */
    private void refreshDataSource(String dsKey) {
        if (getPamirsPersistenceConfiguration().fetchPamirsPersistenceConfiguration(dsKey).getAutoCreateDatabase()) {
            ddlManager.createDatabase(dsKey);
        }
        Map<String, String> dataSourcePropertiesMap = getDataSourceConfiguration().get(dsKey);
        Properties dataSourceProperties = properties(dataSourcePropertiesMap);
        DataSource dataSource = PamirsDataSourceFactory.build(dsKey, dataSourceProperties);
        assert dataSource != null;
        dataSourceHolder.put(dsKey, dataSource);
    }

    /**
     * 刷新sharding数据源
     *
     * @param dsKey 数据源key
     * @throws Exception 异常
     */
    public void refreshShardingDataSource(String dsKey) throws Exception {
        DataSource dataSource = shardingDataSource(dsKey);
        dataSourceHolder.put(dsKey, dataSource);
    }

    private void refresh(String beanName) {
        org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope =
                (org.springframework.cloud.context.scope.refresh.RefreshScope) BeanDefinitionUtils.getBean(SystemBeanConstants.REFRESH_SCOPE);
        refreshScope.refresh(beanName);
    }

    private Properties properties(Map<String, String> properties) {
        Properties p = new Properties();
        for (String key : properties.keySet()) {
            p.setProperty(key, TypeUtils.stringValueOf(properties.get(key)));
        }
        return p;
    }

    public DataSource shardingDataSource(String dsKey) throws SQLException {
        if (StringUtils.isBlank(dsKey)) {
            throw PamirsException.construct(DataExpEnumerate.BASE_SHARDING_CONFIG_ERROR).errThrow();
        }
        ShardingSpecificConfiguration configuration = getShardingConfiguration().get(dsKey);
        Collection<RuleConfiguration> ruleConfigurations = SWAPPER_ENGINE.swapToRuleConfigurations(configuration.getRules());
        return ShardingSphereDataSourceFactory.createDataSource(shardingDataSourceMap(configuration.getActualDs()),
                ruleConfigurations, configuration.getProps());
    }

    private Map<String, DataSource> shardingDataSourceMap(String... names) {
        Map<String, DataSource> shardingDataSourceMap = new HashMap<>();
        for (String name : names) {
            DataSource dataSource = dataSourceHolder.get(name);
            if (null == dataSource) {
                throw PamirsException.construct(BASE_DATASOURCE_CONFIG_IS_NOT_EXISTS_ERROR).appendMsg("ds:" + name).errThrow();
            }
            shardingDataSourceMap.put(name, dataSource);
        }
        return shardingDataSourceMap;
    }

    private PamirsPersistenceConfiguration getPamirsPersistenceConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.PAMIRS_PERSISTENCE_CONFIGURATION, PamirsPersistenceConfiguration.class);
    }

    private DataSourceConfiguration getDataSourceConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.DATA_SOURCE_CONFIGURATION, DataSourceConfiguration.class);
    }

    private ShardingRuleConfiguration getShardingConfiguration() {
        return BeanDefinitionUtils.getBean(SystemBeanConstants.SHARDING_RULE_CONFIGURATION, ShardingRuleConfiguration.class);
    }

}

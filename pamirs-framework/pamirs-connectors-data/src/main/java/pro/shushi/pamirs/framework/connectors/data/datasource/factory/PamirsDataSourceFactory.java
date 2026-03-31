package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 5:19 下午
 */
@Slf4j
public class PamirsDataSourceFactory {

    private static Map<String, DataSourceBuilder> builderMap;

    private static Map<String, DataSourceBuilder> getBuilderMap() {
        Map<String, DataSourceBuilder> builderMap = PamirsDataSourceFactory.builderMap;
        if (builderMap == null) {
            synchronized (PamirsDataSourceFactory.class) {
                builderMap = PamirsDataSourceFactory.builderMap;
                if (builderMap == null) {
                    Map<String, DataSourceBuilder> dataSourceBuilders = BeanDefinitionUtils.getBeansOfType(DataSourceBuilder.class);
                    builderMap = new HashMap<>(dataSourceBuilders.size());
                    for (DataSourceBuilder builder : dataSourceBuilders.values()) {
                        builderMap.put(builder.key(), builder);
                    }
                    PamirsDataSourceFactory.builderMap = builderMap;
                }
            }
        }
        return builderMap;
    }

    public static DataSource build(String dsKey, Properties properties) {
        Map<String, DataSourceBuilder> builderMap = getBuilderMap();
        String isBean = properties.getProperty(DbConstants.BEAN);
        if (StringUtils.isBlank(isBean) || Boolean.TRUE.toString().equals(isBean)) {
            isBean = Boolean.TRUE.toString();
        } else {
            isBean = Boolean.FALSE.toString();
        }
        properties.remove(DbConstants.BEAN);
        String type = properties.getProperty(DbConstants.FIELD_TYPE);
        if (StringUtils.isBlank(type)) {
            type = DefaultDataSourceBuilder.KEY;
        }
        // 设置数据源默认最优配置
        String useDefaultProperties = properties.getProperty(DbConstants.USE_DEFAULT_PROPERTIES);
        if (!Boolean.FALSE.toString().equals(useDefaultProperties)) {
            properties.putIfAbsent("initialSize", "5");
            properties.putIfAbsent("maxActive", "20");
            properties.putIfAbsent("minIdle", "5");
            properties.putIfAbsent("maxWait", "60000");
            properties.putIfAbsent("timeBetweenEvictionRunsMillis", "60000");
            properties.putIfAbsent("testWhileIdle", "true");
            properties.putIfAbsent("testOnBorrow", "false");
            properties.putIfAbsent("testOnReturn", "false");
            properties.putIfAbsent("poolPreparedStatements", "false");
            properties.putIfAbsent("maxOpenPreparedStatements", "-1");
            properties.putIfAbsent("asyncInit", "true");
        }
        type = type + CharacterConstants.SEPARATOR_OCTOTHORPE + isBean;
        DataSourceBuilder builder = builderMap.get(type);
        if (null == builder) {
            builder = builderMap.get(DefaultDataSourceBuilder.KEY);
        }
        if (null != builder) {
            return builder.build(dsKey, properties);
        }
        return null;
    }

}

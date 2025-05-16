package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 默认数据源生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 5:46 下午
 */
@Slf4j
@Component
public class DefaultDataSourceBuilder implements DataSourceBuilder {

    public static final String KEY = "default";

    @Override
    public DataSource build(String dsKey, Properties properties) {
        return defaultBuild(dsKey, properties);
    }

    @SuppressWarnings("unchecked")
    public static DataSource defaultBuild(String dsKey, Properties properties) {
        try {
            org.springframework.boot.jdbc.DataSourceBuilder<?> dataSourceBuilder = org.springframework.boot.jdbc.DataSourceBuilder.create();
            String type = properties.getProperty(DbConstants.FIELD_TYPE);
            if (StringUtils.isNotBlank(type)) {
                dataSourceBuilder.type((Class<DataSource>) Class.forName(type));
            }
            return dataSourceBuilder
                    .driverClassName(properties.getProperty(DbConstants.FIELD_DRIVER))
                    .url(properties.getProperty(DbConstants.FIELD_URL))
                    .username(properties.getProperty(DbConstants.FIELD_USERNAME))
                    .password(properties.getProperty(DbConstants.FIELD_PASSWORD))
                    .build();
        } catch (Throwable e) {
            throw PamirsException.construct(DataExpEnumerate.BASE_CLASS_DRIVER_ERROR, e).errThrow();
        }
    }

    @Override
    public String key() {
        return KEY;
    }

}

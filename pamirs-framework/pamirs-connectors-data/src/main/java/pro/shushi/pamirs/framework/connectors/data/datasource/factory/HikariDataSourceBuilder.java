package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.constant.DbConstants;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hikari数据源生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 7:56 下午
 */
@Slf4j
@Component
public class HikariDataSourceBuilder implements DataSourceBuilder {

    private static final String TYPE = "com.zaxxer.hikari.HikariDataSource";

    private static final String KEY = TYPE + CharacterConstants.SEPARATOR_OCTOTHORPE + Boolean.FALSE;

    @Override
    public DataSource build(String dsKey, Properties properties) {
        properties.put(DbConstants.FIELD_TYPE, TYPE);
        return DefaultDataSourceBuilder.defaultBuild(dsKey, properties);
    }

    @Override
    public String key() {
        return KEY;
    }
}

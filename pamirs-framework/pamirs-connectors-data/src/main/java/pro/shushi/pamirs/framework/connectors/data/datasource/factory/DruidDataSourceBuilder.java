package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.sql.DataSource;
import java.util.Properties;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_CLASS_DRUID_DRIVER_ERROR;

/**
 * Druid数据源生成器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 6:21 下午
 */
@Slf4j
@Component
public class DruidDataSourceBuilder implements DataSourceBuilder {

    private static final String KEY = "com.alibaba.druid.pool.DruidDataSource" + CharacterConstants.SEPARATOR_OCTOTHORPE + Boolean.FALSE;

    @Override
    public DataSource build(String dsKey, Properties properties) {
        try {
            return DruidDataSourceFactory.createDataSource(properties);
        } catch (Throwable e) {
            throw PamirsException.construct(BASE_CLASS_DRUID_DRIVER_ERROR, e).errThrow();
        }
    }

    @Override
    public String key() {
        return KEY;
    }
}

package pro.shushi.pamirs.framework.connectors.data.datasource.factory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 生成数据源接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/10 5:20 下午
 */
public interface DataSourceBuilder {

    DataSource build(String dsKey, Properties properties);

    String key();

}

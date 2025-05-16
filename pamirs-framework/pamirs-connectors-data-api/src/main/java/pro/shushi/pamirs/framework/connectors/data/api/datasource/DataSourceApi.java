package pro.shushi.pamirs.framework.connectors.data.api.datasource;

import javax.sql.DataSource;

/**
 * 数据源API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:02 上午
 */
public interface DataSourceApi {

    DataSource get(Object ds);

}

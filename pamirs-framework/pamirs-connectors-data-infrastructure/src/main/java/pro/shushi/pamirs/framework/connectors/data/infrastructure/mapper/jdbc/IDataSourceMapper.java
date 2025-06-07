package pro.shushi.pamirs.framework.connectors.data.infrastructure.mapper.jdbc;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Database;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.DatabaseInstance;

/**
 * 物理基础设施接口
 * <p>
 * 2020/7/31 4:01 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface IDataSourceMapper {

    /**
     * 获取数据库实例信息
     *
     * @param dsKey 数据源key
     * @return 数据库信息
     */
    DatabaseInstance fetchDatabaseInstanceInfo(String dsKey);

    /**
     * 获取数据库实例中指定名称数据库信息
     *
     * @param dsKey 数据源key
     * @return 数据库信息
     */
    Database fetchDatabaseInfo(String dsKey);

}

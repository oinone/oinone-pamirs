package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 数据存储元数据方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaDataDialectService {

    /**
     * 获取已存在表结构
     *
     * @param modelTableMap 模型与表的映射
     * @param databaseMap   数据库与数据源的映射
     * @param supportDrop   支持删除表和字段
     * @return 已存在的表结构
     */
    Map<String/*schema#table*/, LogicTable> fetchLogicTableMap(Map<String/*schema#table*/, ModelTable> modelTableMap,
                                                               Map<String/*schema*/, String/*ds*/> databaseMap,
                                                               boolean supportDrop);

}

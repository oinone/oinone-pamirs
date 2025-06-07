package pro.shushi.pamirs.framework.connectors.data.mapper.batch;

import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;
import pro.shushi.pamirs.meta.api.enmu.BatchOpTypeEnum;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Pamirs 通用mapper接口
 * <p>
 * 2020-01-09 00:22
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PamirsBatchApi {

    /**
     * 批量操作
     *
     * @param opType     批量操作类型
     * @param entityList 实体对象列表
     * @param function   操作函数
     * @return 影响行数
     */
    @SuppressWarnings("rawtypes")
    int batchCommit(BatchOpTypeEnum opType, List entityList, BiFunction<PamirsMapper, Object, Integer> function);

}

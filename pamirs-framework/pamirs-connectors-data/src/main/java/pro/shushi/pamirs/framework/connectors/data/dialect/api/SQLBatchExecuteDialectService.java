package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.function.Function;

/**
 * 批量执行方言服务
 *
 * @author Adamancy Zhang at 21:56 on 2024-01-18
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SQLBatchExecuteDialectService {

    <T> Integer batchSubmit(Function<List<T>, Integer> function, List<T> entityList);

}

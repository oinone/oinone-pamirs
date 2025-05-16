package pro.shushi.pamirs.meta.api.core.orm.systems.orm.spi;

import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldRecursionComputeApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * ORM对象化处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("rawtypes")
@SPI
public interface OrmObjectingProcessorApi extends ModelBeforeComputeApi, ModelAfterComputeApi,
        FieldRecursionComputeApi {

}

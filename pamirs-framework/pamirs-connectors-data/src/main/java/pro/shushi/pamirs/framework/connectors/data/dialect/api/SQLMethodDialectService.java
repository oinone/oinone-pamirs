package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.InsertMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.SelectMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.UpdateMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 脚本执行方言服务
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SQLMethodDialectService {

    SelectMethod getSelectMethod(ModelConfig modelConfig);

    InsertMethod getInsertMethod(ModelConfig modelConfig);

    UpdateMethod getUpdateMethod(ModelConfig modelConfig);

    DeleteMethod getDeleteMethod(ModelConfig modelConfig);
}

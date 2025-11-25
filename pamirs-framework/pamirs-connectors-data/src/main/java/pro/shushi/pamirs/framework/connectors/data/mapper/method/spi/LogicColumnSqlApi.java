package pro.shushi.pamirs.framework.connectors.data.mapper.method.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 逻辑列fillSqlSegment
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface LogicColumnSqlApi {

    /**
     * 逻辑列SQL片段扩展
     */
    String LogicColumnScript(String model, String column, String property);

}

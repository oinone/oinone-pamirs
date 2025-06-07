package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 方言组件接口包列表API
 * <p>
 * 2020/8/3 1:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DialectPackageApi {

    List<String> packages();

}

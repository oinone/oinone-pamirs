package pro.shushi.pamirs.meta.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.lang.reflect.Method;

/**
 * 函数命名空间和函数编码获取API
 *
 * @author Adamancy Zhang at 14:13 on 2025-08-29
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface NamespaceAndFunFetcher {

    String getBeanName(Method method);

}

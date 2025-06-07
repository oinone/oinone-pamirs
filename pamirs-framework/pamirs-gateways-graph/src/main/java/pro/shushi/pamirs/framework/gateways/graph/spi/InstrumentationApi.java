package pro.shushi.pamirs.framework.gateways.graph.spi;

import graphql.execution.instrumentation.Instrumentation;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 拦截器spi
 * <p>
 * 2021/3/26 7:19 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface InstrumentationApi {

    Instrumentation build();

}

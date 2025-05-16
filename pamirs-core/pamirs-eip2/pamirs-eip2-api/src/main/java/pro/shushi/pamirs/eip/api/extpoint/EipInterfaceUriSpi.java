package pro.shushi.pamirs.eip.api.extpoint;

import org.apache.camel.Exchange;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * uri扩展点
 *
 * @deprecated 请使用动态URL参数转换进行URI编辑
 */
@Deprecated
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EipInterfaceUriSpi {

    String computeUri(String uri, Exchange exchange, IEipContext<SuperMap> context);

}

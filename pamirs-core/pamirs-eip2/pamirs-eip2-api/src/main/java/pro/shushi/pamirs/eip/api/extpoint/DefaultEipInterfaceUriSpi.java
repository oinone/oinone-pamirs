package pro.shushi.pamirs.eip.api.extpoint;

import org.apache.camel.Exchange;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @deprecated 请使用动态URL参数转换进行URI编辑
 */
@Deprecated
@SPI.Service
@Order
@Component
public class DefaultEipInterfaceUriSpi implements EipInterfaceUriSpi {

    @Override
    public String computeUri(String uri, Exchange exchange, IEipContext<SuperMap> context) {
        return uri;
    }

}
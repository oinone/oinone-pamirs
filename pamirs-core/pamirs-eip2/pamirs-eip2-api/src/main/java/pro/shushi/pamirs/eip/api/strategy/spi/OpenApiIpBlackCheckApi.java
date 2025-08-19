package pro.shushi.pamirs.eip.api.strategy.spi;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 开放接口IP黑名单校验服务
 * @author yeshenyue on 2025/4/11 19:36.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface OpenApiIpBlackCheckApi {

    /**
     * 开放接口IP黑名单校验
     */
    Boolean check(IEipContext<SuperMap> context, ExtendedExchange exchange);
}

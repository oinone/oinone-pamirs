package pro.shushi.pamirs.user.core.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.model.tmodel.TopBarUserBlock;
import pro.shushi.pamirs.user.api.spi.TopBarUserBlockDataApi;

/**
 * 自定义扩展TopBarUserBlock
 */
@Deprecated
@Order
@Component
@SPI.Service
public class DefaultTopBarUserBlockDataApi implements TopBarUserBlockDataApi {


    @Override
    public TopBarUserBlock extendData(TopBarUserBlock data) {
        return data;
    }
}

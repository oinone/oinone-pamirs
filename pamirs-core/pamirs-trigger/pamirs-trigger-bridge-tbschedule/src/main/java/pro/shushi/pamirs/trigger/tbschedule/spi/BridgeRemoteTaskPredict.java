package pro.shushi.pamirs.trigger.tbschedule.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.trigger.spi.RemoteTaskPredictApi;

/**
 * 桥接
 *
 * @author Adamancy Zhang at 14:52 on 2021-08-10
 */
@Component
@Order(0)
@SPI.Service
public class BridgeRemoteTaskPredict implements RemoteTaskPredictApi {

    @Override
    public boolean isRemote() {
        return false;
    }
}

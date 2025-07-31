package pro.shushi.pamirs.eip.api.auth;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.auth.api.EipLogSaveApi;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.dsl.signal.Obj;

/*
 * DefaultEipLogSave
 * @author : Haibo(xf.z@shushi.pro)
 * @date : 2025/7/8 14:53
 */

@Slf4j
@Order
@Component
@SPI.Service
public class DefaultEipLogSave implements EipLogSaveApi {


    @Override
    public EipLog saveLog(EipLog eipLog, IEipContext<SuperMap> context) {
        return eipLog.create();
    }
}

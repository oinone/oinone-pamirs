package pro.shushi.pamirs.eip.api.auth.api;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/*
 * EIp日志保存扩展
 * EipLogSaveApi
 * @author : Haibo(xf.z@shushi.pro)
 * @date : 2025/7/8 14:50
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface EipLogSaveApi {

    /**
     * 开放接口IP白名单校验
     */
     EipLog saveLog(EipLog eipLog,Object context);
}

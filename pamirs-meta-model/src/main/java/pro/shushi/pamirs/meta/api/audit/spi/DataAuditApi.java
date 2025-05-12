package pro.shushi.pamirs.meta.api.audit.spi;

import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 扩展审计日志：请求信息，IP，机器名，Fun相关信息
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataAuditApi {

    /**
     * 根据参数计算审计日志信息
     * @param uriType
     * @param namespace
     * @param fun
     * @return
     */
    void computeDataAuditSession(UriType uriType, String namespace, String fun, String traceId);

}

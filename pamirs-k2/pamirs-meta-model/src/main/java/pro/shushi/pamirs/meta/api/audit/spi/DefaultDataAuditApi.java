package pro.shushi.pamirs.meta.api.audit.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.enmu.UriType;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 扩展审计日志：请求信息，IP，机器名，Fun相关信息
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultDataAuditApi implements DataAuditApi {

    @Override
    public void computeDataAuditSession(UriType uriType, String namespace, String fun, String traceId) {
    }
}

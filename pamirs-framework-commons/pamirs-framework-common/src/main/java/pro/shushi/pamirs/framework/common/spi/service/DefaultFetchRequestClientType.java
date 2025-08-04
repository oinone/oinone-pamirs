package pro.shushi.pamirs.framework.common.spi.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.FetchRequestClientTypeApi;
import pro.shushi.pamirs.framework.common.utils.header.UserAgentUtil;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 获取请求客户端类型默认实现
 *
 * @author Adamancy Zhang at 10:39 on 2024-11-19
 */
@Order
@Component
@SPI.Service
public class DefaultFetchRequestClientType implements FetchRequestClientTypeApi {

    @Override
    public ClientTypeEnum fetchCurrentClientType(HttpServletRequest request, String moduleName, PamirsRequestParam requestParam) {
        String userAgent = requestParam.getVariables().getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            return ClientTypeEnum.PC;
        }
        return UserAgentUtil.parseClientType(userAgent);
    }
}

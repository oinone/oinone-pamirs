package pro.shushi.pamirs.boot.common.spi.service.session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.spi.SessionAfterApi;
import pro.shushi.pamirs.framework.common.utils.CookieUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 默认session处理实现
 *
 * @author cpc on 2024-01-13
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultSessionAfter implements SessionAfterApi {

    @Override
    public void after(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = PamirsSession.getSessionId();
        if (StringUtils.isNoneBlank(sessionId)) {
            CookieUtil.set(response, CookieUtil.USER_SESSION_ID, sessionId);
        }
    }
}


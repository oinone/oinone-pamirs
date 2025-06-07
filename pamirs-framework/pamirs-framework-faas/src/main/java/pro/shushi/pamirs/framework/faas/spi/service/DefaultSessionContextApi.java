package pro.shushi.pamirs.framework.faas.spi.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.expression.SessionContextApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.SessionContext;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

import static pro.shushi.pamirs.meta.constant.ExpressionContextConstants.CONTEXT;

/**
 * 获取表达式session上下文SPI默认实现
 * <p>
 * 2021/3/4 11:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultSessionContextApi implements SessionContextApi {

    @Override
    public SessionContext context(Map<String, Object> contextMap) {
        SessionContext context = (SessionContext) contextMap.get(CONTEXT);
        if (null == context) {
            context = new SessionContext();
        }
        String env = PamirsSession.getEnv();
        String module = PamirsSession.getServApp();
        String lang = PamirsSession.getLang();
        String country = PamirsSession.getCountry();
        return context.setEnv(env)
                .setModule(module)
                .setRequestFromModule(PamirsSession.getRequestFromModule())
                .setLang(lang)
                .setCountry(country);
    }

}

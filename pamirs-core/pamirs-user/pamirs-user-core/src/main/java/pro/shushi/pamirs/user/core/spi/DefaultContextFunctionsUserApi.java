package pro.shushi.pamirs.user.core.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionsUserApi;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.service.UserService;

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
public class DefaultContextFunctionsUserApi implements ContextFunctionsUserApi {

    @Override
    public Object currentUser() {
        return CommonApiFactory.getApi(UserService.class).queryById(PamirsSession.getUserId());
    }

}

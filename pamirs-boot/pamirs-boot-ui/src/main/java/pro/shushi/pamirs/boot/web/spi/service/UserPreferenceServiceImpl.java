package pro.shushi.pamirs.boot.web.spi.service;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.spi.api.UserPreferenceService;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 11:59
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@SPI.Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Override
    public String load(ViewAction viewAction, View view) {
        return null;
    }
}

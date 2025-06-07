package pro.shushi.pamirs.framework.common.init;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.Spider;

import javax.annotation.PostConstruct;

/**
 * 万物生
 * <p>
 * 2020/6/19 6:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class PamirsInit {

    public static final String BEAN_NAME = "pamirsInit";

    @SuppressWarnings("unused")
    @PostConstruct
    void init() {
        Spider.getDefaultExtension(PamirsInitBeforeApi.class).preAction();
    }

}

package pro.shushi.pamirs.meta.api.core.session;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.session.thread.MainThreadSessionClearApi;
import pro.shushi.pamirs.meta.api.core.session.thread.SubThreadSessionClearApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;

/**
 * 会话清理服务
 *
 * @author Adamancy Zhang at 17:05 on 2024-04-26
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionClearService {

    /**
     * {@link MainThreadSessionClearApi}
     */
    default void clearMainSession() {
        List<MainThreadSessionClearApi> sessionClearApiList = BeanDefinitionUtils.getBeansOfTypeByOrdered(MainThreadSessionClearApi.class);
        if (!CollectionUtils.isEmpty(sessionClearApiList)) {
            for (MainThreadSessionClearApi sessionClearApi : sessionClearApiList) {
                sessionClearApi.clear();
            }
        }
    }

    /**
     * {@link SubThreadSessionClearApi}
     */
    default void clearSubSession() {
        List<SubThreadSessionClearApi> sessionClearApiList = BeanDefinitionUtils.getBeansOfTypeByOrdered(SubThreadSessionClearApi.class);
        if (!CollectionUtils.isEmpty(sessionClearApiList)) {
            for (SubThreadSessionClearApi sessionClearApi : sessionClearApiList) {
                sessionClearApi.clear();
            }
        }
    }

    /**
     * {@link SessionClearApi}
     */
    default void clear() {
        List<SessionClearApi> sessionClearApiList = BeanDefinitionUtils.getBeansOfTypeByOrdered(SessionClearApi.class);
        if (!CollectionUtils.isEmpty(sessionClearApiList)) {
            for (SessionClearApi sessionClearApi : sessionClearApiList) {
                sessionClearApi.clear();
            }
        }
    }

}

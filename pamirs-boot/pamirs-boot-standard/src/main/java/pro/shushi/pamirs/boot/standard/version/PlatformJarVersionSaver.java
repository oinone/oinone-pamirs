package pro.shushi.pamirs.boot.standard.version;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.PlatformJarVersionCheckerApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;
import pro.shushi.pamirs.locale.utils.I18nUtils;

import jakarta.annotation.Resource;

/**
 * PlatformJarVersionSaver
 *
 * @author yakir on 2024/07/22 11:35.
 */
@Slf4j
@Component
@Order
public class PlatformJarVersionSaver implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private PlatformJarVersionCheckerApi platformJarVersionCheckerApi;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        TimeWatcher.watch(
                platformJarVersionCheckerApi::store,
                I18nUtils.getMessage("PlatformJarVersionSaver.storeJarVersionInfo")
        );
    }
}

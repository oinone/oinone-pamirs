package pro.shushi.pamirs.resource.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.core.common.LifecycleExecutorHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.resource.api.ResourceModule;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;
import pro.shushi.pamirs.resource.api.spi.api.ResourceSystemInitializationIcon;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR;

@Slf4j
@Component
public class ResourceIconInitLoader implements InstallDataInit, UpgradeDataInit {

    private static final String PATH = "classpath*:/pamirs/init/icon/iconfont/*.zip";

    @Autowired(required = false)
    @Qualifier(FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        onlyInit();
        return true;
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        onlyInit();
        return true;
    }

    private void onlyInit() {
        LifecycleExecutorHelper.execute(globalFixedThreadPoolExecutor, () -> {
            try {
                initIcon();
            } catch (Throwable t) {
                log.error("Icon initialization failed, please pay close attention!", t);
            }
        });
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(ResourceModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }

    private void initIcon() {
        PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver(AppClassLoader.getClassLoader(ResourceIconInitLoader.class));
        try {
            Resource[] resources = finder.getResources("classpath*:/pamirs/init/icon/iconfont/*.zip");
            for (Resource resource : resources) {
                try {
                    Spider.getExtension(ResourceSystemInitializationIcon.class, IconLibTypeEnum.ICONFONT.getValue()).writeData(resource);
                } catch (Throwable e) {
                    log.error("Decompress file failed", e);
                }
            }
            String language = I18nUtils.getLocale().getLanguage();
            resources = finder.getResources(String.format("classpath*:/pamirs/init/icon/iconfont/%s/*.zip", language));
            for (Resource resource : resources) {
                try {
                    Spider.getExtension(ResourceSystemInitializationIcon.class, IconLibTypeEnum.ICONFONT.getValue()).writeData(resource);
                } catch (Throwable e) {
                    log.error("Decompress file failed", e);
                }
            }
        } catch (IOException e) {
            log.error("Read resource failed", e);
        }

    }
}

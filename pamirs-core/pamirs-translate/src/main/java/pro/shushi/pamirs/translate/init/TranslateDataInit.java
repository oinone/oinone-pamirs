package pro.shushi.pamirs.translate.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.core.common.LifecycleExecutorHelper;
import pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.translate.TranslateModule;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Adamancy Zhang
 * @date 2020-11-04 17:47
 */
@Slf4j
@Order
@Component
public class TranslateDataInit implements InstallDataInit, UpgradeDataInit {

    @Autowired
    private SystemTranslationItemInit systemTranslationItemInit;

    @Autowired(required = false)
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        init();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        init();
        return true;
    }

    private void init() {
        LifecycleExecutorHelper.execute(globalFixedThreadPoolExecutor, () -> {
            systemTranslationItemInit.init();
        });
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(TranslateModule.MODULE_MODULE);
    }

}

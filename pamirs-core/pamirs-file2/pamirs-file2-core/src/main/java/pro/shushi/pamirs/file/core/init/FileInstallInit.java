package pro.shushi.pamirs.file.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.core.common.LifecycleExecutorHelper;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.file.api.config.FileOSSConfiguration;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInitHelper;
import pro.shushi.pamirs.framework.common.config.AsyncTaskExecutorConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;


/**
 * @Author: Wuer
 * @email: syj@shushi.pro
 * @Date: 2020/4/26 4:25 下午
 * @Description: 文件模块安装初始化数据
 */
@Slf4j
@Component
public class FileInstallInit implements InstallDataInit, UpgradeDataInit {

    @Autowired
    private FileOSSConfiguration configuration;

    @Autowired(required = false)
    @Qualifier(AsyncTaskExecutorConfiguration.FIXED_THREAD_POOL_EXECUTOR)
    private Executor globalFixedThreadPoolExecutor;

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        initialization();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initialization();
        return true;
    }

    private void initialization() {
        LifecycleExecutorHelper.execute(globalFixedThreadPoolExecutor, () -> {
            try {
                ExcelTemplateInitHelper.init();
            } catch (Throwable t) {
                log.error("文件模板初始化失败，请高度关注！", t);
            }
        });
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(FileModule.MODULE_MODULE);
    }
}

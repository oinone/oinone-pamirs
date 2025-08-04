package pro.shushi.pamirs.boot.common.initial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.PamirsBootMainProcessApi;
import pro.shushi.pamirs.boot.common.api.command.AppCommand;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleOptions;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootConditionApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.boot.common.util.ApplicationArgUtils;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.CheckStrategyEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 主启动类
 * <p>
 * 2021/2/25 1:41 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class PamirsBootMainInitial {

    private static final Logger log = LoggerFactory.getLogger(PamirsBootMainInitial.class);

    @Resource
    private ApplicationArguments applicationArguments;

    @Resource
    private PamirsBootMainProcessApi pamirsBootMainProcessApi;

    @EventListener
    @Order(5)
    @SuppressWarnings({"unused"})
    public void lifecycle(ContextRefreshedEvent event) {
        ApplicationArgUtils.handle(applicationArguments.getSourceArgs());
    }

    @EventListener
    @Order(100)
    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    public void init(ApplicationStartedEvent event) throws InterruptedException, ExecutionException {
        BootConditionApi bootConditionApi = Spider.getDefaultExtension(BootConditionApi.class);
        if (bootConditionApi.needLoad()) {
            if (bootConditionApi.isSync()) {// 非主线程同步执行
                CompletableFuture.runAsync(this::installOrLoad).get();
            } else {// 异步执行
                ExecutorService computeExecutor = Executors.newSingleThreadExecutor();
                ExecutorCompletionService<Boolean> executorCompletionService = new ExecutorCompletionService(computeExecutor);
                executorCompletionService.submit(this::installOrLoad);
                executorCompletionService.take().get();
            }
        }
    }

    /**
     * 初始化加载，自动创建或者变更表结构
     *
     * @return 初始化结果
     */
    public Boolean installOrLoad() {
        PamirsSession.clear();

        BootModulesApi bootModulesApi = Spider.getDefaultExtension(BootModulesApi.class);
        Set<String> modules = bootModulesApi.modules();
        Set<String> excludeModules = bootModulesApi.excludeModules();

        // 获取jar包和数据库中模块安装包
        Map<String, ModuleDefinition> setupModuleMap = pamirsBootMainProcessApi.fetchSetupModuleMap();

        // 获取启动模块列表
        Set<String> bootModuleSet = pamirsBootMainProcessApi.fetchBootModules(setupModuleMap, modules, excludeModules);
        ModulesApi modulesApi = Spider.getDefaultExtension(ModulesApi.class);
        modulesApi.setModules(bootModuleSet);

        BootConditionApi bootConditionApi = Spider.getDefaultExtension(BootConditionApi.class);
        AppLifecycleOptions appLifecycleOptions = Optional.ofNullable(bootConditionApi.options())
                .map(AppLifecycleOptions::deepClone)
                .orElse(new AppLifecycleOptions());
        try {
            // 准备生命周期命令
            AppCommand appCommand = ApplicationArgUtils.getCommand();
            if (null == appCommand) {
                appCommand = new AppCommand(bootConditionApi.install(), bootConditionApi.upgrade(), bootConditionApi.profile());
            }
            AppLifecycleCommand appLifecycleCommand = AppLifecycleCommand.init(
                    appCommand.getInstallEnum(),
                    appCommand.getUpgradeEnum(),
                    appCommand.getProfile(),
                    appLifecycleOptions
            ).config(ApplicationArgUtils.getArgs());

            log.info("\nBoot Options: {}", JsonUtils.toJSONString(appLifecycleCommand, true));

            ComputeContext computeContext = ComputeContext.init()
                    .setCheckStrategy(CheckStrategyEnum.RETURN_WHEN_COMPLETED)
                    .setMsgLevel(InformationLevelEnum.DEBUG)
                    .setCheckField(Optional.ofNullable(ApplicationArgUtils.getArgs().getCheckField()).orElse(false));
            return pamirsBootMainProcessApi.installOrLoad(computeContext, bootModuleSet, null, appLifecycleCommand);
        } finally {
            PamirsSession.clear();
        }
    }

}

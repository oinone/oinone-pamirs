package pro.shushi.pamirs.boot.common.process;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.PamirsBootMainProcessApi;
import pro.shushi.pamirs.boot.common.api.command.AppArgs;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleOptions;
import pro.shushi.pamirs.boot.common.api.contants.MetaOnlineEnum;
import pro.shushi.pamirs.boot.common.api.init.*;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleGroup;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleTriple;
import pro.shushi.pamirs.boot.common.spi.api.boot.*;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataInstallApi;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataReloadApi;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataResolveApi;
import pro.shushi.pamirs.boot.common.spi.api.data.ModuleDataUpgradeApi;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.*;
import pro.shushi.pamirs.boot.common.spi.api.meta.*;
import pro.shushi.pamirs.boot.common.supplier.MetaDataSupplier;
import pro.shushi.pamirs.boot.common.util.ApplicationArgUtils;
import pro.shushi.pamirs.boot.common.util.MetaBootCountDown;
import pro.shushi.pamirs.framework.configure.sys.MetaModelFetcherCache;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.faas.boot.ModulesApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;
import pro.shushi.pamirs.meta.common.util.SetUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模块生命周期管理流程
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class PamirsBootMainProcessor implements PamirsBootMainProcessApi {

    @Override
    public Boolean installOrLoad(final ComputeContext context, final Set<String> modules, final Set<String> excludeModules,
                                 AppLifecycleCommand command) {
        //元数据在线模式需要校验ownSign
        this.checkMetaOnlineOwnSign(command);

        // 获取jar包和数据库中模块安装包
        Map<String, ModuleDefinition> setupModuleMap = fetchSetupModuleMap();
        Set<String> bootModuleSet = fetchBootModules(setupModuleMap, modules, excludeModules);

        Spider.getDefaultExtension(BootModuleLifecycleBeginAllApi.class).run(command, setupModuleMap);

        // 模块生命周期处理
        Spider.getDefaultExtension(BootModuleLifecycleAroundApi.class).run(command, context, setupModuleMap, bootModuleSet, (_command, _context, _setupModuleMap, _bootModuleSet) -> {
            // 系统初始化
            List<BootSystemInitApi> bootSystemInitApis = Spider.getLoader(BootSystemInitApi.class).getOrderedExtensions();
            for (BootSystemInitApi bootSystemInitApi : bootSystemInitApis) {
                bootSystemInitApi.init(_command);
            }

            Spider.getDefaultExtension(BootModulesExtApi.class).dataModules(_command, _setupModuleMap, _bootModuleSet);
            Spider.getDefaultExtension(ModulesApi.class).setModules(_bootModuleSet);
            final Set<String> runModuleSet = SetUtils.newNonNullSet(_bootModuleSet);

            // 计算模块生命周期状态
            LifecycleModuleGroup lifecycleModuleGroup = Spider.getDefaultExtension(BootModuleLifecycleApi.class).states(_command, _setupModuleMap, runModuleSet);
            List<ModuleDefinition> installModules = lifecycleModuleGroup.getInstallModules();
            List<ModuleDefinition> upgradeModules = lifecycleModuleGroup.getUpgradeModules();
            List<ModuleDefinition> reloadModules = lifecycleModuleGroup.getReloadModules();
            log.info("install modules: {}", installModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toList()));
            log.info("upgrade modules: {}", upgradeModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toList()));
            log.info("reload modules: {}", reloadModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toList()));

            Map<String, ModuleDefinition> moduleInfoMap = lifecycleModuleGroup.getModuleInfoMap();
            Map<String, ModuleDefinition> installedModuleInfoMap = lifecycleModuleGroup.getInstalledModuleInfoMap();

            Spider.getDefaultExtension(BootModuleLifecycleBeginApi.class).run(_command, runModuleSet, installModules, upgradeModules, reloadModules);
            Set<String> upgradeModuleSet = upgradeModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toSet());
            Set<String> reloadModuleSet = reloadModules.stream().map(ModuleDefinition::getModule).collect(Collectors.toSet());

            // 元数据加载与编辑器
            MetaDataLoaderApi metaDataLoadApi = Spider.getDefaultExtension(MetaDataLoaderApi.class);
            MetaDataReLoaderApi metaDataReLoadApi = Spider.getDefaultExtension(MetaDataReLoaderApi.class);

            // 从数据库加载元数据
            Map<String/*module*/, MetaData> upgradeModuleMap = metaDataReLoadApi
                    .load(_command, upgradeModuleSet, (obj) -> {
                        if (Boolean.TRUE.equals(obj.getSys()) || SystemSourceEnum.isInherited(obj.getSystemSource())) {
                            // 低代码元数据和继承元数据需经过元数据计算后才能确认是否保留
                            Models.modelDirective().enableMetaCompleted(obj);
                        }
                        Models.modelDirective().enableMetaDiffing(obj);
                    });
            Map<String/*module*/, MetaData> reloadModuleMap = metaDataReLoadApi
                    .load(_command, reloadModuleSet, (obj) -> Models.modelDirective().disableMetaCompleted(obj));

            // 跨模块挂载元数据
            Map<String, MetaData> allMetaDataMap = new LinkedHashMap<>(reloadModuleMap);
            allMetaDataMap.putAll(upgradeModuleMap);
            metaDataReLoadApi.crossingLoadMetaData(allMetaDataMap);

            // 从注解加载元数据
            Map<String/*module*/, Meta> allMetaMap = metaDataLoadApi.load(_command, runModuleSet, null,
                    moduleInfoMap, upgradeModuleMap, reloadModuleMap);

            Map<String/*module*/, Meta> metaMap = new LinkedHashMap<>();
            Map<String/*module*/, Meta> runMetaMap = new LinkedHashMap<>();
            Set<String> sortedModule = new LinkedHashSet<>();
            List<Meta> metaList = new ArrayList<>();
            for (Map.Entry<String, Meta> entry : allMetaMap.entrySet()) {
                String module = entry.getKey();
                Meta meta = entry.getValue();
                if (runModuleSet.contains(module)) {
                    sortedModule.add(module);
                    runMetaMap.put(module, meta);
                }
                metaList.add(meta);
                metaMap.put(module, meta);
            }

            // 计算元数据
            MetaDataComputerApi metaDataCompute = Spider.getDefaultExtension(MetaDataComputerApi.class);
            metaDataCompute.compute(_command, _context, metaList, null);

            // 编程式编辑元数据
            long timer = System.currentTimeMillis();
            Spider.getDefaultExtension(MetaDataEditApi.class).edit(_command, metaMap);
            log.info("metadata editor cost time: {}ms", System.currentTimeMillis() - timer);

            // 元数据差量减(包括删除分布式Session)
            timer = System.currentTimeMillis();
            Spider.getDefaultExtension(MetaDataUpgraderApi.class).diffDelete(_command, metaList, runModuleSet, reloadModules);
            log.info("metadata upgrade cost time: {}ms", System.currentTimeMillis() - timer);

            // 填充session(包括填充分布式Session)
            timer = System.currentTimeMillis();
            metaDataLoadApi.loadSessionFromMeta(_command, runModuleSet, metaList);
            metaDataLoadApi.loadSessionModules(_command, moduleInfoMap, metaList);
            log.info("metadata load cost time: {}ms", System.currentTimeMillis() - timer);

            // 构建http接口协议
            timer = System.currentTimeMillis();
            Spider.getDefaultExtension(HttpApiBuilderApi.class).build(_command, runModuleSet, metaList);
            log.info("http api builder cost time: {}ms", System.currentTimeMillis() - timer);

            // 构建表结构
            timer = System.currentTimeMillis();
            Spider.getDefaultExtension(TableBuilderApi.class).build(_command, runMetaMap, runModuleSet);
            log.info("table builder cost time: {}ms", System.currentTimeMillis() - timer);

            // 扩展构建
            timer = System.currentTimeMillis();
            Spider.getDefaultExtension(ExtendBuilderApi.class).build(_command, runMetaMap);
            log.info("extend builder cost time: {}ms", System.currentTimeMillis() - timer);

            // 系统初始化数据前置处理
            Map<String, SystemBootDataInit> systemBootDataInitMap = BeanDefinitionUtils.getBeansOfType(SystemBootDataInit.class);
            List<SystemBootDataInit> systemBootDataInitials = new ArrayList<>(Objects.requireNonNull(systemBootDataInitMap).values());
            systemBootDataInitials.sort(Comparator.comparingInt(Prioritized::priority));
            Spider.getDefaultExtension(MetaDataBeforeSaverApi.class).before(_command, systemBootDataInitials);

            // 保存元数据
            Spider.getDefaultExtension(MetaDataSaverApi.class).save(_command, runMetaMap, reloadModuleSet);

            // 系统初始化数据后置处理
            Map<String, SystemBootAfterInit> systemBootAfterInitMap = BeanDefinitionUtils.getBeansOfType(SystemBootAfterInit.class);
            List<SystemBootAfterInit> systemBootAfterInitials = new ArrayList<>(Objects.requireNonNull(systemBootAfterInitMap).values());
            systemBootAfterInitials.sort(Comparator.comparingInt(Prioritized::priority));
            Spider.getDefaultExtension(MetaDataAfterSaverApi.class).after(_command, systemBootAfterInitials);

            // 后置扩展构建
            Spider.getDefaultExtension(ExtendAfterBuilderApi.class).build(_command, runMetaMap);

            // 获取各生命周期模块配置
            LifecycleModuleTriple lifecycleModuleTriple = Spider.getDefaultExtension(ModuleDataResolveApi.class)
                    .resolve(_command, runModuleSet, sortedModule, installModules, upgradeModules, reloadModules);
            List<ModuleDefinition> runInstallModules = lifecycleModuleTriple.getInstallModules();
            List<ModuleDefinition> runUpgradeModules = lifecycleModuleTriple.getUpgradeModules();
            List<ModuleDefinition> runReloadModules = lifecycleModuleTriple.getReloadModules();

            // 处理安装模块初始化
            Map<String, InstallDataInit> installDataInitMap = BeanDefinitionUtils.getBeansOfType(InstallDataInit.class);
            List<InstallDataInit> installDataInitials = new ArrayList<>(Objects.requireNonNull(installDataInitMap).values());
            installDataInitials.sort(Comparator.comparingInt(Prioritized::priority));
            Spider.getDefaultExtension(ModuleDataInstallApi.class).install(_command, runInstallModules, installDataInitials);

            // 处理升级模块初始化
            Map<String, UpgradeDataInit> upgradeDataInitMap = BeanDefinitionUtils.getBeansOfType(UpgradeDataInit.class);
            List<UpgradeDataInit> upgradeDataInitials = new ArrayList<>(Objects.requireNonNull(upgradeDataInitMap).values());
            upgradeDataInitials.sort(Comparator.comparingInt(Prioritized::priority));
            Spider.getDefaultExtension(ModuleDataUpgradeApi.class).upgrade(_command, installedModuleInfoMap, runUpgradeModules, upgradeDataInitials);

            // 处理加载模块初始化
            Map<String, ReloadDataInit> reloadDataInitMap = BeanDefinitionUtils.getBeansOfType(ReloadDataInit.class);
            List<ReloadDataInit> reloadDataInitials = new ArrayList<>(Objects.requireNonNull(reloadDataInitMap).values());
            reloadDataInitials.sort(Comparator.comparingInt(Prioritized::priority));
            Spider.getDefaultExtension(ModuleDataReloadApi.class).reload(_command, runReloadModules, reloadDataInitials);

            // 后置生命周期扩展
            Spider.getDefaultExtension(ExtendLifecycleApi.class).extend(_command, runMetaMap);

            Spider.getDefaultExtension(BootModuleLifecycleCompletedApi.class).run(_command, runInstallModules, runUpgradeModules, runReloadModules);

            // 元数据是否执行完成的标识(Redis完成标识，需要等待并发全部完成)
            MetaBootCountDown.judge();
            MetaDataSupplier.clear();
            MetaModelFetcherCache.clear();
        });

        Spider.getDefaultExtension(BootModuleLifecycleCompletedAllApi.class).run(command, setupModuleMap);

        return true;
    }

    private Map<String, Meta> fetchRunMetaMap(Set<String> runModuleSet, List<Meta> metaList) {
        Map<String, Meta> runMetaMap = new LinkedHashMap<>();
        metaList.forEach(v -> {
            if (runModuleSet.contains(v.getModule())) {
                runMetaMap.put(v.getModule(), v);
            }
        });
        return runMetaMap;
    }

    @Override
    public Map<String, ModuleDefinition> fetchSetupModuleMap() {
        BootModulesApi bootModulesApi = Spider.getDefaultExtension(BootModulesApi.class);
        Map<String, ModuleDefinition> setupModuleMap = new HashMap<>();
        Map<String, ModuleDefinition> jarModuleMap = bootModulesApi.jarModules();
        setupModuleMap.putAll(jarModuleMap);
        return setupModuleMap;
    }

    @Override
    public Set<String> fetchBootModules(Map<String, ModuleDefinition> setupModuleMap, Set<String> modules, Set<String> excludeModules) {
        if (CollectionUtils.isEmpty(modules)) {
            modules = setupModuleMap.keySet();
        }
        boolean isEmptyExcludeSet = CollectionUtils.isEmpty(excludeModules);
        if (isEmptyExcludeSet) {
            return modules;
        }
        Set<String> bootModuleSet = new ConcurrentHashSet<>(setupModuleMap.size());
        for (String module : modules) {
            if (excludeModules.contains(module)) {
                continue;
            }
            bootModuleSet.add(module);
        }
        return bootModuleSet;
    }

    //元数据在线的情况，必须配置ownSign
    private void checkMetaOnlineOwnSign(AppLifecycleCommand command) {
        String cdOwnSign = Spider.getDefaultExtension(SessionFillOwnSignApi.class).getCdOwnSign();
        if (StringUtils.isNotBlank(cdOwnSign)) {
            AppArgs args = ApplicationArgUtils.getArgs();
            args.setMetaOnline(MetaOnlineEnum.NEVER);
            AppLifecycleOptions options = command.getOptions();
            options.setUpdateModule(false);
            options.setReloadMeta(true);
            options.setUpdateMeta(false);
        }
    }

}

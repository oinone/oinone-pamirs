package pro.shushi.pamirs.boot.standard.spi;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.boot.common.domain.LifecycleModuleGroup;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModelPrepareApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModuleLifecycleApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.framework.configure.annotation.core.ModuleDependencyResolver;
import pro.shushi.pamirs.framework.configure.db.service.ModuleService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.VersionProcessor;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import jakarta.annotation.Resource;
import java.util.*;

import static pro.shushi.pamirs.boot.common.enmu.BootExpEnumerate.*;

/**
 * 启动模块生命周期状态接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(66)
@Component
@SPI.Service
public class StandardBootModuleLifecycle implements BootModuleLifecycleApi {

    @Resource
    private ModuleService moduleService;

    @Resource
    private VersionProcessor versionProcessor;

    @Override
    public LifecycleModuleGroup states(AppLifecycleCommand command, Map<String, ModuleDefinition> setupModuleMap, Set<String> runModules) {

        // 临时构造模块的模型定义
        Map<String/*model*/, String/*simulate model*/> modelMap = new HashMap<>();
        Spider.getDefaultExtension(BootModelPrepareApi.class).prepare(modelMap);
        // 判断是否注册模块信息
        boolean reloadModule = command.getOptions().isReloadModule();
        boolean checkModule = command.getOptions().isCheckModule();
        boolean updateModule = command.getOptions().isUpdateModule();
        boolean updateMeta = command.getOptions().isUpdateMeta();
        boolean rebuildTable = command.getOptions().isRebuildTable();
        boolean diffTable = command.getOptions().isDiffTable();
        // 查询模块数据
        Map<String, ModuleDefinition> dbModuleMap = fetchModuleMapFromDB(modelMap,
                reloadModule, updateModule, updateMeta, rebuildTable, diffTable);

        Map<String, ModuleDefinition> installModuleMap = new HashMap<>();
        Map<String, ModuleDefinition> upgradeModuleMap = new HashMap<>();
        Map<String, ModuleDefinition> reloadModuleMap = new HashMap<>();
        Map<String, ModuleDefinition> moduleInfoMap = new HashMap<>();
        Map<String, ModuleDefinition> installedModuleInfoMap = new HashMap<>();

        moduleInfoMap.putAll(dbModuleMap);
        moduleInfoMap.putAll(setupModuleMap);
        Set<String> moduleSet = setupModuleMap.keySet();
        if (!moduleSet.containsAll(runModules)) {
            Set<String> errorModuleSet = new HashSet<>(runModules);
            errorModuleSet.removeAll(moduleSet);
            throw PamirsException.construct(BASE_BOOT_MODULE_IN_JAR_ERROR)
                    .appendMsg("error modules:" + JsonUtils.toJSONString(errorModuleSet)
                            + ",run modules:" + JsonUtils.toJSONString(runModules)
                            + ",total modules:" + JsonUtils.toJSONString(moduleSet)).errThrow();
        }

        InstallEnum installEnum = command.getInstallEnum();
        UpgradeEnum upgradeEnum = command.getUpgradeEnum();
        log.info("Module lifecycle upgrade status: {}", upgradeEnum.name());
        Set<String> completedModule = new HashSet<>();
        // 计算模块生命周期
        for (String module : runModules) {
            moduleLifecycleCompute(runModules, null, setupModuleMap, dbModuleMap,
                    installModuleMap, upgradeModuleMap, reloadModuleMap, installEnum, upgradeEnum,
                    completedModule, module, checkModule);
            ModuleDefinition moduleDefinitionFromDB = dbModuleMap.get(module);
            if (null != moduleDefinitionFromDB
                    && (ModuleStateEnum.INSTALLED.equals(moduleDefinitionFromDB.getState())
                    //安装失败的应用会把状态设为TOUPGRADE，因为TOUPGRADE也是安装过的应用
                    || ModuleStateEnum.TOUPGRADE.equals(moduleDefinitionFromDB.getState()))) {
                installedModuleInfoMap.put(module, dbModuleMap.get(module));
            }
        }
        Set<String> waitSetupModule = new HashSet<>(completedModule);
        // 计算模块依赖互斥
        for (String module : waitSetupModule) {
            checkExclusionConflict(moduleInfoMap, installedModuleInfoMap, module);
            ModuleDependencyResolver.consumeModuleDependency(moduleInfoMap, moduleInfoMap.get(module),
                    dependentModule -> {
                        boolean isUnCompleted = !completedModule.contains(dependentModule.getModule());
                        if (isUnCompleted) {
                            completedModule.add(dependentModule.getModule());
                        }
                        return isUnCompleted;
                    },
                    (currentModule, dependentModule) -> {
                        checkExclusionConflict(moduleInfoMap, installedModuleInfoMap, dependentModule.getModule());
                        moduleLifecycleCompute(null, runModules, setupModuleMap, dbModuleMap,
                                installModuleMap, upgradeModuleMap, reloadModuleMap, installEnum, upgradeEnum,
                                completedModule, dependentModule.getModule(), checkModule);
                    });
        }

        return (LifecycleModuleGroup) new LifecycleModuleGroup()
                .setModuleInfoMap(moduleInfoMap)
                .setInstalledModuleInfoMap(installedModuleInfoMap)
                .setInstallModules(new ArrayList<>(installModuleMap.values()))
                .setUpgradeModules(new ArrayList<>(upgradeModuleMap.values()))
                .setReloadModules(new ArrayList<>(reloadModuleMap.values()));
    }

    private Map<String, ModuleDefinition> fetchModuleMapFromDB(Map<String, String> modelMap,
                                                               boolean reloadModule,
                                                               boolean updateModule,
                                                               boolean updateMeta,
                                                               boolean rebuildTable,
                                                               boolean diffTable) {
        Map<String, ModuleDefinition> dbModuleMap;
        if (reloadModule) {
            Set<String> distributionModules = Spider.getDefaultExtension(BootModulesApi.class).distributionModules();
            QueryWrapper<ModuleDefinition> queryWrapper = Pops.<ModuleDefinition>query().from(ModuleDefinition.MODEL_MODEL);
            if (!CollectionUtils.isEmpty(distributionModules)) {
                queryWrapper.in(VariableNameConstants.module, distributionModules);
            }
            dbModuleMap = moduleService.fetchModuleMapFromDB(modelMap, queryWrapper,
                    (modulesDefinition) -> {
                        if (!rebuildTable || !updateModule && !updateMeta) {
                            return;
                        }
                        LogicSchemaService.get().buildTable(Lists.newArrayList(modulesDefinition), false, diffTable);
                    });
        } else {
            dbModuleMap = new HashMap<>();
        }
        return dbModuleMap;
    }

    private void moduleLifecycleCompute(Set<String> runModules,
                                        Set<String> upgradeModules,
                                        Map<String, ModuleDefinition> setupModuleMap,
                                        Map<String, ModuleDefinition> dbModuleMap,
                                        Map<String, ModuleDefinition> installModuleMap,
                                        Map<String, ModuleDefinition> upgradeModuleMap,
                                        Map<String, ModuleDefinition> reloadModuleMap,
                                        InstallEnum installEnum, UpgradeEnum upgradeEnum,
                                        Set<String> completedModule, String module,
                                        boolean checkModuleState) {
        ModuleDefinition currentModuleDefinition = fetchModuleDefinition(setupModuleMap, dbModuleMap, module);
        if (null != runModules && !runModules.contains(module)
                && (null == currentModuleDefinition.getBoot() || !currentModuleDefinition.getBoot())) {
            return;
        }
        if (!dbModuleMap.containsKey(module)) {
            if (checkModuleState && InstallEnum.READONLY.equals(installEnum)) {
                throw PamirsException.construct(BASE_BOOT_RELOAD_MODULE_NOT_INSTALLED_ERROR)
                        .appendMsg("module:" + module).errThrow();
            }
            if (isValidateInstallEnum(installEnum)) {
                installModuleMap.put(module, currentModuleDefinition);
            }
        } else {
            ModuleDefinition moduleDefinitionFormDB = dbModuleMap.get(module);
            ModuleStateEnum moduleStateEnum = moduleDefinitionFormDB.getState();
            currentModuleDefinition.setId(moduleDefinitionFormDB.getId());
            if (ModuleStateEnum.UNINSTALLABLE.equals(moduleStateEnum)) {
                reloadModuleMap.put(module, moduleDefinitionFormDB);
            } else if (ModuleStateEnum.UNINSTALLED.equals(moduleStateEnum)
                    || ModuleStateEnum.TOINSTALL.equals(moduleStateEnum)) {
                if (checkModuleState && InstallEnum.READONLY.equals(installEnum)) {
                    throw PamirsException.construct(BASE_BOOT_RELOAD_MODULE_NOT_INSTALLED2_ERROR)
                            .appendMsg("module:" + module).errThrow();
                }
                if (isValidateInstallEnum(installEnum)) {
                    installModuleMap.put(module, currentModuleDefinition);
                }
            } else if (ModuleStateEnum.TOUPGRADE.equals(moduleStateEnum)) {
                if (UpgradeEnum.READONLY.equals(upgradeEnum)) {
                    reloadModuleMap.put(module, moduleDefinitionFormDB);
                } else {
                    upgradeModuleMap.put(module, currentModuleDefinition);
                }
            } else if (ModuleStateEnum.TOREMOVE.equals(moduleStateEnum)) {
                return;
            } else if (ModuleStateEnum.INSTALLED.equals(moduleStateEnum)) {
                if (UpgradeEnum.FORCE.equals(upgradeEnum)) {
                    // 仅升级指定运行模块，其他模块仅加载
                    if (upgradeModules == null || upgradeModules.contains(module)) {
                        upgradeModuleMap.put(module, currentModuleDefinition);
                    } else {
                        reloadModuleMap.put(module, currentModuleDefinition);
                    }
                } else if (UpgradeEnum.AUTO.equals(upgradeEnum)) {
                    boolean isNewVersion = versionProcessor.isNewVersion(moduleDefinitionFormDB.getLatestVersion(),
                            currentModuleDefinition.getLatestVersion());
                    if (isNewVersion) {
                        upgradeModuleMap.put(module, currentModuleDefinition);
                    } else {
                        reloadModuleMap.put(module, moduleDefinitionFormDB);
                    }
                } else if (UpgradeEnum.READONLY.equals(upgradeEnum)) {
                    reloadModuleMap.put(module, moduleDefinitionFormDB);
                }
            } else {
                return;
            }
        }
        completedModule.add(module);
    }

    private boolean isValidateInstallEnum(InstallEnum installEnum) {
        return InstallEnum.AUTO.equals(installEnum) || InstallEnum.READONLY.equals(installEnum);
    }

    private ModuleDefinition fetchModuleDefinition(Map<String, ModuleDefinition> setupModuleMap, Map<String, ModuleDefinition> dbModuleMap, String module) {
        ModuleDefinition newModuleDefinition = setupModuleMap.get(module);
        if (null == newModuleDefinition) {
            newModuleDefinition = dbModuleMap.get(module);
            if (null == newModuleDefinition) {
                throw PamirsException.construct(BASE_BOOT_MODULE_MISSING_ERROR)
                        .appendMsg("module:" + module).errThrow();
            }
        }
        return newModuleDefinition;
    }

    private void checkExclusionConflict(Map<String, ModuleDefinition> moduleInfoMap, Map<String, ModuleDefinition> installedModuleInfoMap, String module) {
        ModuleDependencyResolver.consumeModuleExclusion(moduleInfoMap, moduleInfoMap.get(module),
                (currentModule, exclusionModule) -> {
                    if (installedModuleInfoMap.containsKey(exclusionModule.getModule())) {
                        throw PamirsException.construct(BASE_BOOT_EXCLUSION_MODULE_CONFLICT_ERROR)
                                .appendMsg("module:" + currentModule.getModule()
                                        + "exclusion module:" + exclusionModule.getModule()).errThrow();
                    }
                });
    }

}

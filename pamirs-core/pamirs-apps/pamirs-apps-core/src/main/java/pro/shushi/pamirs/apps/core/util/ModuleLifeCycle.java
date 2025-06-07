package pro.shushi.pamirs.apps.core.util;//package pro.shushi.pamirs.apps.util;
//
//import com.google.common.collect.Lists;
//import org.apache.commons.collections4.CollectionUtils;
//import pro.shushi.pamirs.apps.enmu.ExpEnumerate;
//import pro.shushi.pamirs.framework.connectors.event.enumeration.ModuleLifeCycleEnum;
//import pro.shushi.pamirs.framework.connectors.event.util.*;
//import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
//import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
//import pro.shushi.pamirs.meta.base.TransientModel;
//import pro.shushi.pamirs.meta.common.exception.PamirsException;
//import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
//import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class ModuleLifeCycle {
//
/// /    @ModelField(ttype = ModelTtypeEnum.one2many, relation = "pro.shushi.pamirs.base.model.meta.Module")
/// /    private List<Module> modules;
//
//    /** 应用安装*/
//    public ModuleDefinition install(ModuleDefinition module) {
////        List<ModuleDefinition> modules = new ArrayList<>();
////        try {
//////            String groupName = PamirsEnvironment.getModuleGroup().getGroupName();
////            // check
////            module = ModuleUtils.fetchModuleByName(module.getName());
////            CheckModuleUtils.checkModulesStatus(module);
////            CheckModuleUtils.checkInstallModule(module);
////
////            List<ModuleDefinition> moduleDependencies = ModuleDependencyUtils.spreadDependencyModulesFromDB(module);
////            modules = fetchInstallModule(moduleDependencies);
//////            prepareTenancyDataSources(modules);
////            List<ModuleDefinition> finalModules = modules;
////            updateGroupState(finalModules, ModuleStateEnum.INSTALLED.getValue());
////            for (ModuleDefinition m:modules) {
//////                new MakerEngine().makerRunWithBuildGraphQL(m, ModuleLifeCycleEnum.INSTALL);
////            }
////            updateAppsModule(module);
////            //RELOAD所有的依赖模块 场景eg：贪婪继承
////            module = ModuleUtils.fetchModuleByName(module.getName());
////            LifeCycleMessageUtils.buildAndSendReloadEvent(moduleDependencies, ModuleLifeCycleEnum.RELOAD, PamirsTenantSession.getTenant(), Boolean.FALSE);
//////            LifeCycleMessageUtils.buildAndSendGroupDeploy(finalModules, groupName, PamirsCacheRead.getTenancy().getCode(), ModuleInstallStatusEnum.SUCCESS.getValue());
////            return module;
////        } catch (Exception e) {
////            throw PamirsException.construct(ExpEnumerate.BASE_ModuleLifeCycle_INSTALL_UNKNOWN_ERROR, e).errThrow();
////        }
//        return module;
//    }
//
//    /** 容器重启*/
//    public ModuleDefinition reload(ModuleDefinition module) {
////        try {
////            ModuleDefinition finalModule = PreviewCommandT.run(() -> ModuleUtils.fetchModuleByName(module.getName()));
//////            prepareTenancyDataSources(Lists.newArrayList(finalModule));
////            updateGroupState(Lists.newArrayList(finalModule), finalModule.getState());
////            new MakerEngine().makerRunWithBuildGraphQL(finalModule, ModuleLifeCycleEnum.RELOAD);
////            return finalModule;
////        } catch (Exception e) {
////            throw PamirsException.construct(ExpEnumerate.BASE_ModuleLifeCycle_RELOAD_UNKNOWN_ERROR, e).errThrow();
////        }
//        return module;
//    }
//
//    /** 预览环境容器重启*/
//    public ModuleDefinition reloadPreview(ModuleDefinition module) {
////        try {
////            PreviewCommand.run(() -> {
////                ModuleDefinition modulePre = ModuleUtils.fetchModuleByName(module.getName());
////                //CheckModuleUtils.checkReloadModule(modulePre);
//////                prepareTenancyDataSources(Lists.newArrayList(modulePre));
////                new MakerEngine().makerRun0(modulePre, ModuleLifeCycleEnum.RELOAD);
////            });
////            return module;
////        } catch (Exception e) {
////            throw PamirsException.construct(ExpEnumerate.BASE_ModuleLifeCycle_RELOAD_PREVIEW_UNKNOWN_ERROR, e).errThrow();
////        }
//        return module;
//    }
//
//
//    /** 应用升级*/
//    public ModuleDefinition upgrade(ModuleDefinition module) {
////        try {
////            String groupName = PamirsEnvironment.getModuleGroup().getGroupName();
////            String tenancy = PamirsCacheRead.getTenancy().getCode();
////            List<ModuleDefinition> modules = PreviewCommandT.run(() -> fetchUpgradeModule(module, tenancy));
////            PreviewCommand.run(() -> ModuleUtils.updateModuleState(modules, ModuleStateEnum.TOUPGRADE));
////            for (ModuleDefinition m:modules) {
////                new MakerEngine().makerRunWithBuildGraphQL(m, ModuleLifeCycleEnum.UPGRADE);
////            }
////            updateAppsModule(module);
////            LifeCycleMessageUtils.buildAndSendReloadEvent(modules, ModuleLifeCycleEnum.RELOAD, tenancy, Boolean.FALSE);
////            return module;
////        } catch (Exception e) {
////            throw PamirsException.construct(ExpEnumerate.BASE_ModuleLifeCycle_UPGRADE_UNKNOWN_ERROR, e).errThrow();
////        }
//        return module;
//    }
//
//
//    /** 应用预览环境升级*/
//    public ModuleDefinition upgradePreview(ModuleDefinition module) {
////        try {
////            String tenancy = PamirsTenantSession.getTenant();
////            PreviewCommand.run(() -> {
////                List<ModuleDefinition> modules = fetchUpgradeModule(module, tenancy);
////                for (ModuleDefinition m:modules) {
////                    new MakerEngine().makerRunWithBuildGraphQL(m, ModuleLifeCycleEnum.UPGRADE);
////                    LifeCycleMessageUtils.buildAndSendReloadEvent(m, ModuleLifeCycleEnum.RELOAD, tenancy, Boolean.TRUE);
////                }
////            });
////            return module;
////        } catch (Exception e) {
////            throw PamirsException.construct(ExpEnumerate.BASE_ModuleLifeCycle_UPGRADE_PREVIEW_UNKNOWN_ERROR, e).errThrow();
////        }
//        return module;
//    }
//
//    private void updateAppsModule(ModuleDefinition module) {
////        List<Map<String, Object>> previewModuleList = PreviewCommandT.run(()->((JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC)
////                .get(ModuleDefinition.class.getName(), DataOpTypeEnum.READ)).findAll(new HashMap(){{put("name", module.getName());}}), Boolean.TRUE);
////        PreviewCommand.run(()-> BatchDiffSave.getInstance(MakerConstants.BaseModuleName).storeDataForJdbc(Module.class.getName(), previewModuleList), Boolean.FALSE);
//    }
//
//    /** 获取module 1、从数据库 2、studio从group中 */
//    private List<ModuleDefinition> fetchInstallModule(List<ModuleDefinition> moduleDependencys) throws Exception {
////        moduleDependencys= ModuleDependencyTree.buildSequenceModules(moduleDependencys);
//        List<ModuleDefinition> installModules = moduleDependencys.stream().filter(v -> v.getState() == null ? Boolean.TRUE : ModuleStateEnum.UNINSTALLED.getValue().equals(v.getState())).collect(Collectors.toList());
////
////        Map<String, ModuleDefinition> localModules = PamirsEnvironment.getModuleGroup().getModules().stream().collect(Collectors.toMap(Module::getName, v -> v));
////        List<ModuleDefinition> remoteModules = installModules.stream().filter(v -> !localModules.containsKey(v.getName())).collect(Collectors.toList());
////        installModules = installModules.stream().filter(v -> localModules.containsKey(v.getName())).collect(Collectors.toList());
////        if (CollectionUtils.isNotEmpty(remoteModules)) {
////            LifeCycleMessageUtils.buildAndSendInstallEvent(remoteModules, ModuleLifeCycleEnum.INSTALL,
////                    , PamirsTenantSession.getTenant(), Boolean.FALSE);
////        }
//
//        return installModules;
//    }
//
//    private List<ModuleDefinition> fetchUpgradeModule(ModuleDefinition module, String tenancy) throws Exception {
////        String groupName = PamirsEnvironment.getModuleGroup().getGroupName();
////
////        module = ModuleUtils.fetchModuleByName(module.getName());
////        CheckModuleUtils.checkModulesStatus(module);
////        CheckModuleUtils.checkUpgradeModule(module);
////
////        //检查本地jar包的发行版本是否一致
////        Map<String, ModuleDefinition> localModules = PamirsEnvironment.getModuleGroup().getModules().stream().collect(Collectors.toMap(ModuleDefinition::getName, v -> v));
////        Module localModule = localModules.get(module.getName());
////        if (VersionControl.isNewVersion(localModule.getPublishedVersion(), module.getLatestVersion())) {
////            Module finalModule = module;
////            PreviewCommand.run(() -> LifeCycleMessageUtils.buildAndSendGroupDeploy(
////                    Lists.newArrayList(finalModule)
////                    , PamirsEnvironment.getModuleGroup().getGroupName()
////                    , tenancy
////                    , ModuleInstallStatusEnum.SUCCESS.getValue()), Boolean.FALSE);
////            throw log.error(ExpEnumerate.BASE_JAR_MODULE_NOT_SUPPORT_ERROR, "the version of current module is inconsistent with online version").errThrow();
////        }
////
////        module.setModuleDependencies(null);
//        List<ModuleDefinition> dependencys = ModuleDependencyUtils.spreadDependencyModulesFromDB(module);
//        List<ModuleDefinition> upgradeModules = dependencys.stream().filter(v -> VersionControl.isNewVersion(v.getLatestVersion(), v.getPublishedVersion())).collect(Collectors.toList());
////        List<ModuleDefinition> installModules = dependencys.stream().filter(v -> ModuleStateEnum.UNINSTALLED.getValue().equals(v.getState())).collect(Collectors.toList());
////
////        if (CollectionUtils.isNotEmpty(installModules)) {
////            LifeCycleMessageUtils.buildAndSendInstallEvent(
////                    installModules
////                    , ModuleLifeCycleEnum.INSTALL
////                    , tenancy
////                    , Boolean.FALSE);
////        }
////
////        List<ModuleDefinition> remoteModules = upgradeModules.stream().filter(v -> !localModules.containsKey(v.getName())).collect(Collectors.toList());
////        upgradeModules = upgradeModules.stream().filter(v -> localModules.containsKey(v.getName())).collect(Collectors.toList());
////        if (CollectionUtils.isNotEmpty(remoteModules)) {
////            LifeCycleMessageUtils. buildAndSendUpgradeEvent(
////                    remoteModules
////                    , ModuleLifeCycleEnum.UPGRADE
////                    , tenancy
////                    , Boolean.FALSE);
////        }
//        return upgradeModules;
//    }
//
////    private void prepareTenancyDataSources(List<ModuleDefinition> modules) {
////        for (ModuleDefinition module : modules) {
////            prepareTenancyDataSources(module);
////            // 准备预览数据源
////            PreviewCommand.run(() -> prepareTenancyDataSources(module));
////        }
////    }
//
//    /** 安装重启module时准备datasource*/
////    private void prepareTenancyDataSources(Module module) {
////        Tenancy tenancy = PamirsCacheRead.getTenancy();
////        PamirsCacheWrite.putLocalModule(module);
////        for (DataEngineTypeEnum dataEngineTypeEnum : DataEngineTypeEnum.values()) {
////            for (DataOpTypeEnum dataOpTypeEnum : DataOpTypeEnum.values()) {
////                TenancyDataSources tenancyDataSourceBase = PamirsCacheRead
////                        .getTenancyDataSource(Boolean.FALSE, MakerConstants.BaseModuleName, dataEngineTypeEnum, dataOpTypeEnum);
////                List<DataSources> dataSources = Lists.newArrayList(tenancyDataSourceBase.getDataSources());
////                TenancyDataSources tenancyDataSourcesPreview = DataSourceUtils.newTenancyDataSource(module.getName(), dataEngineTypeEnum, dataOpTypeEnum, tenancy, dataSources);
////                PamirsCacheWrite.setTenancyDataSource(module.getName(), dataEngineTypeEnum, dataOpTypeEnum, tenancyDataSourcesPreview);
////            }
////        }
////    }
//
////    /** 修改moduleGroup的状态 */
////    public static void updateGroupState(List<ModuleDefinition> modules, String state) {
////        for (ModuleDefinition module : modules) {
////            List<ModuleDefinition> localModule = PamirsEnvironment.getModuleGroup().getModules();
////            Map<String, ModuleDefinition> localModuleMap = localModule.stream().collect(Collectors.toMap(ModuleDefinition::getName, v->v));
////            ModuleDefinition moduleGroup = localModuleMap.get(module.getName());
////            if (null != moduleGroup) {
////                moduleGroup.setState(state);
////                moduleGroup.setPublishedVersion(module.getPublishedVersion());
////                moduleGroup.setLatestVersion(String.valueOf(module.getPublishedVersion()));
////            }
////        }
////    }
////
////    public void unistallModuleModelData(ModuleDefinition module) throws Exception {
////        JdbcRepository modelDataReposity = ((JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(ModelData.class.getName(), DataOpTypeEnum.READ));
////        List<Map<String, Object>> modelDataMaps = modelDataReposity.findAll(new HashMap(){{put("module", module.getName());}});
////        Set<Long> ids = modelDataMaps.stream().map(v -> Long.valueOf(String.valueOf(v.get(SqlConstants.ID)))).collect(Collectors.toSet());
////        modelDataReposity.deleteById(ParamUtils.setToList(ids));
////    }
//}

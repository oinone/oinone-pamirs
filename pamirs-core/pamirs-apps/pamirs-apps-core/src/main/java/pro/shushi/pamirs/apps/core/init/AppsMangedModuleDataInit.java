package pro.shushi.pamirs.apps.core.init;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.AppsModule;
import pro.shushi.pamirs.apps.api.enmu.ModuleCategoryType;
import pro.shushi.pamirs.apps.api.model.AppsModuleCategory;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.core.common.InitializationUtil;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.MetaSourceEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
import pro.shushi.pamirs.meta.enmu.SoftwareLicenseEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 初始化管理模块数据
 *
 * @author Adamancy Zhang on 2021-05-07 15:26
 */
@Slf4j
@Component
public class AppsMangedModuleDataInit implements MetaDataEditor, SystemBootAfterInit {

    @Value("${pamirs.apps.force-update-default-logo:false}")
    private boolean isForceUpdateDefaultLogo;

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        InitializationUtil util = InitializationUtil.get(metaMap, AppsModule.MODULE_MODULE, AppsModule.MODULE_NAME);
        if (util == null) {
            return;
        }
        init0(util);
    }

    @Override
    public boolean init(AppLifecycleCommand command) {
        initCategory();
        initNotExistManagementModules();
        return true;
    }

    @Override
    public int priority() {
        return 1;
    }

    /**
     * 初始化模块分类
     */
    private void initCategory() {
        List<AppsModuleCategory> moduleCategories = new ArrayList<>();
        ModuleCategoryType[] moduleCategoryTypes = ModuleCategoryType.values();
        for (ModuleCategoryType moduleCategoryType : moduleCategoryTypes) {
            moduleCategories.add(
                    (AppsModuleCategory) new AppsModuleCategory()
                            .setScreenVisible(moduleCategoryType.getScreenVisible())
                            .setCode(moduleCategoryType.getValue())
                            .setName(moduleCategoryType.getDisplayName())
                            .setParent(
                                    StringUtils.isBlank(moduleCategoryType.getParentCode()) ? null : new AppsModuleCategory().setCode(moduleCategoryType.getParentCode())
                            )
                            .setSequence(moduleCategoryType.getSequence())
                            .setVisible(Boolean.TRUE)
            );
        }
        new AppsModuleCategory().createOrUpdateBatch(moduleCategories);
    }

    /**
     * 初始化不存在的管理模块
     */
    private void initNotExistManagementModules() {
        List<UeModule> existModules = Models.origin().queryListByWrapper(Pops.<UeModule>lambdaQuery().from(UeModule.MODEL_MODEL));
        MemoryListSearchCache<String, UeModule> existModuleCache = new MemoryListSearchCache<>(existModules, UeModule::getModule);
        Map<String, UeModule> managementModules = fetchManagementModules();
        List<UeModule> createModules = new ArrayList<>();
        for (Map.Entry<String, UeModule> entry : managementModules.entrySet()) {
            String moduleModule = entry.getKey();
            if (existModuleCache.get(moduleModule) != null) {
                continue;
            }
            UeModule targetModule = entry.getValue();
            targetModule = createModule(moduleModule, targetModule);
            if (targetModule != null) {
                createModules.add(targetModule);
            }
        }
        if (!createModules.isEmpty()) {
            Models.origin().createBatch(createModules);
        }
    }

    private void init0(InitializationUtil util) {
        Map<String, UeModule> managementModules = fetchManagementModules();
        for (Map.Entry<String, UeModule> entry : managementModules.entrySet()) {
            String moduleModule = entry.getKey();
            UeModule updateModule = entry.getValue();
            UeModule ueModule = util.getUeModule(moduleModule);
            if (ueModule != null) {
                updateModule(ueModule, updateModule);
            }
            ModuleDefinition moduleDefinition = util.getModuleDefinition(moduleModule);
            if (moduleDefinition != null) {
                updateModule(moduleDefinition, updateModule);
            }
        }
    }

    private UeModule createModule(String moduleModule, UeModule targetModule) {
        String moduleName = targetModule.getName();
        if (StringUtils.isBlank(moduleName)) {
            return null;
        }
        UeModule module = new UeModule();
        module.setModule(moduleModule);
        module.setName(moduleName);
        module.setDisplayName(targetModule.getDisplayName());
        module.setPriority(targetModule.getPriority());
        module.setApplication(targetModule.getApplication());
        module.setBoot(Boolean.FALSE);
        module.setPlatformVersion(null);
        module.setExcludeHooks(null);
        module.setAuthor(NamespaceConstants.pamirs);
        module.setMaintainer(null);
        module.setContributors(null);
        module.setDemo(Boolean.FALSE);
        module.setWeb(Boolean.TRUE);
        module.setLicense(SoftwareLicenseEnum.PEEL1);
        module.setToBuy(Boolean.TRUE);
        module.setSelfBuilt(Boolean.TRUE);
        module.setMetaSource(MetaSourceEnum.CODE);
        module.setSystemSource(SystemSourceEnum.MANUAL);
        module.setState(ModuleStateEnum.UNINSTALLED);
        updateModule(module, targetModule);
        return module;
    }

    private void updateModule(UeModule module, UeModule targetModule) {
        Optional.ofNullable(targetModule.getPriority()).ifPresent(module::setPriority);
        setStringValue(module, targetModule, UeModule::getSummary, UeModule::setSummary);
        setStringValue(module, targetModule, UeModule::getDescription, UeModule::setDescription);
        setStringValue(module, targetModule, UeModule::getCategory, UeModule::setCategory);
        setModuleLogo(module);
    }

    private void updateModule(ModuleDefinition module, UeModule targetModule) {
        Optional.ofNullable(targetModule.getPriority()).ifPresent(module::setPriority);
        setStringValue(module, targetModule, ModuleDefinition::getSummary, ModuleDefinition::setSummary);
        setStringValue(module, targetModule, ModuleDefinition::getDescription, ModuleDefinition::setDescription);
        setStringValue(module, targetModule, ModuleDefinition::getCategory, ModuleDefinition::setCategory);
    }

    private <T> void setStringValue(T module, T targetModule, Function<T, String> getter, BiConsumer<T, String> setter) {
        if (StringUtils.isNotBlank(getter.apply(module))) {
            return;
        }
        Optional.ofNullable(getter.apply(targetModule)).ifPresent(v -> setter.accept(module, v));
    }

    private void setModuleLogo(UeModule module) {
        setModuleLogo((ModuleDefinition) module);
    }

    private void setModuleLogo(ModuleDefinition module) {
        if (!isForceUpdateDefaultLogo && StringUtils.isNotBlank(module.getDefaultLogo())) {
            return;
        }
        String logoFormat = FileClientFactory.getClient().getStaticUrl() + "/oinone/img/apps/%s@2x.png";
        module.setDefaultLogo(String.format(logoFormat, module.getModule()));
    }

    /**
     * 获取应用中心管理应用
     * <p>
     * setName表示自动创建，不设置则仅更新
     * </p>
     */
    private Map<String, UeModule> fetchManagementModules() {
        Map<String, UeModule> moduleMap = new HashMap<>();

        // 基础能力
        moduleMap.put("base", (UeModule) new UeModule().setName("base").setDisplayName("基础").setSummary("Pamirs平台底座，元数据驱动的基石。").setApplication(Boolean.FALSE).setPriority(100L).setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));
        moduleMap.put("sequence", (UeModule) new UeModule().setName("sequence").setDisplayName("序列").setApplication(Boolean.FALSE).setPriority(100L).setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));
        moduleMap.put("common", (UeModule) new UeModule().setName("common").setDisplayName("公共资源").setApplication(Boolean.FALSE).setPriority(100L).setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));
        moduleMap.put("trigger", (UeModule) new UeModule().setName("trigger").setDisplayName("触发器").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));
        moduleMap.put("message", (UeModule) new UeModule().setName("message").setDisplayName("消息").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));
        moduleMap.put("workbench", (UeModule) new UeModule().setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));

        // 资源
        moduleMap.put("resource", (UeModule) new UeModule().setName("resource").setDisplayName("资源").setApplication(Boolean.TRUE).setPriority(99L).setCategory(ModuleCategoryType.RESOURCE.getValue()));
        moduleMap.put("file", (UeModule) new UeModule().setName("file").setDisplayName("文件").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.RESOURCE.getValue()));

        // 合作伙伴
        moduleMap.put("business", (UeModule) new UeModule().setName("business").setDisplayName("合作伙伴中心").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.PARTNER.getValue()));

        // 用户与权限
        moduleMap.put("auth", (UeModule) new UeModule().setName("auth").setDisplayName("权限").setSummary("多角色的系统管理能力，使用安全的白名单机制，稳定全面的控制您的页面/数据/逻辑行为的安全").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.USER_AND_AUTH.getValue()));
        moduleMap.put("user", (UeModule) new UeModule().setName("user").setDisplayName("用户中心").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.USER_AND_AUTH.getValue()));
        moduleMap.put("management_center", (UeModule) new UeModule().setCategory(ModuleCategoryType.USER_AND_AUTH.getValue()));

        // 国际化
        moduleMap.put("translate", (UeModule) new UeModule().setName("translate").setDisplayName("翻译").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.TRANSLATE.getValue()));

        // 集成
        moduleMap.put("eip", (UeModule) new UeModule().setName("eip").setDisplayName("集成接口").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.OPEN_PLATFORM.getValue()));
        moduleMap.put("tp_map", (UeModule) new UeModule().setName("tpMap").setDisplayName("第三方集成-地图").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.OPEN_PLATFORM.getValue()));
        moduleMap.put("tp_communication", (UeModule) new UeModule().setName("tpCommunication").setDisplayName("第三方集成-通讯").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.OPEN_PLATFORM.getValue()));
//        moduleMap.put("tp_message", (UeModule) new UeModule().setName("tpMessage").setDisplayName("第三方集成-短信").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.OPEN_PLATFORM.getValue()));

        // 数据
        moduleMap.put("data_audit", (UeModule) new UeModule().setName("dataAudit").setDisplayName("数据审计").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.DATA_PLATFORM.getValue()));
//        moduleMap.put("channel", (UeModule) new UeModule().setName("channel").setDisplayName("数据传输").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.DATA_PLATFORM.getValue()));

        // 流程自动化
        moduleMap.put("datavi", (UeModule) new UeModule().setCategory(ModuleCategoryType.PROCESS_AUTOMATION.getValue()));
        moduleMap.put("workflow", (UeModule) new UeModule().setName("workflow").setDisplayName("工作流").setSummary("BPM业务流程管理平台涵盖从流程设计、执行、管理、优化的各个方面完整的流程生命周期支持。").setApplication(Boolean.TRUE).setPriority(100L).setCategory(ModuleCategoryType.PROCESS_AUTOMATION.getValue()));

        // 设计器
        moduleMap.put("designer_common", (UeModule) new UeModule().setName("designerCommon").setDisplayName("设计器公共").setApplication(Boolean.FALSE).setPriority(100L).setCategory(ModuleCategoryType.DESIGNER_COMMON.getValue()));
        moduleMap.put("model_designer", (UeModule) new UeModule().setName("modelDesigner").setDisplayName("模型设计器").setApplication(Boolean.TRUE).setPriority(210L).setCategory(ModuleCategoryType.BUSINESS_PROCESS.getValue()));
        moduleMap.put("ui_designer", (UeModule) new UeModule().setName("uiDesigner").setDisplayName("界面设计器").setApplication(Boolean.TRUE).setPriority(220L).setCategory(ModuleCategoryType.USER_EXPERIENCE.getValue()));
        moduleMap.put("workflow_designer", (UeModule) new UeModule().setName("workflowDesigner").setDisplayName("流程设计器").setApplication(Boolean.TRUE).setPriority(230L).setCategory(ModuleCategoryType.BUSINESS_PROCESS.getValue()));
        moduleMap.put("eip_designer", (UeModule) new UeModule().setName("eipDesigner").setDisplayName("集成设计器").setApplication(Boolean.TRUE).setPriority(240L).setCategory(ModuleCategoryType.BUSINESS_PROCESS.getValue()));
        moduleMap.put("ai_designer", (UeModule) new UeModule().setName("aiDesigner").setDisplayName("AI集成设计器").setApplication(Boolean.TRUE).setPriority(250L).setCategory(ModuleCategoryType.USER_EXPERIENCE.getValue()));
        moduleMap.put("data_designer", (UeModule) new UeModule().setName("dataDesigner").setDisplayName("数据可视化").setApplication(Boolean.TRUE).setPriority(260L).setCategory(ModuleCategoryType.USER_EXPERIENCE.getValue()));
        moduleMap.put("microflow_designer", (UeModule) new UeModule().setName("microflowDesigner").setDisplayName("微流设计器").setApplication(Boolean.TRUE).setPriority(270L).setCategory(ModuleCategoryType.BUSINESS_PROCESS.getValue()));

        // 应用中心
        moduleMap.put("apps", (UeModule) new UeModule().setCategory(ModuleCategoryType.BASE_CAPABILITY.getValue()));

        return moduleMap;
    }
}

package pro.shushi.pamirs.translate.manager.base;

import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.translate.constant.TranslateConstants;

import java.util.*;

/**
 * TranslateModuleManager
 *
 * @author yakir on 2023/09/27 17:23.
 */
public class TranslateModuleManager {

    private List<ModuleDefinition> moduleList;

    public TranslateModuleManager(Boolean application) {
        LambdaQueryWrapper<ModuleDefinition> qw = Pops.<ModuleDefinition>lambdaQuery()
                .from(ModuleDefinition.MODEL_MODEL)
                .select(ModuleDefinition::getModule, ModuleDefinition::getName, ModuleDefinition::getDisplayName, ModuleDefinition::getModuleDependencies)
                .setBatchSize(-1);
        if (Boolean.TRUE.equals(application)) {
            qw.eq(ModuleDefinition::getApplication, true)
                    .or(item -> item.eq(ModuleDefinition::getModule, TranslateConstants.PUBLIC_RESOURCE));
        }
        moduleList = new ModuleDefinition().queryList(qw);
    }

    public Map<String, String> getDisplayNameModuleMap() {

        Map<String, String> displayNameModuleMap = new HashMap<>();
        for (ModuleDefinition moduleDefinition : Optional.ofNullable(moduleList).orElse(Collections.emptyList())) {
            String displayName = moduleDefinition.getDisplayName();
            if (null == displayName) {
                continue;
            }
            displayNameModuleMap.put(displayName, moduleDefinition.getModule());
        }
        return displayNameModuleMap;
    }

    public Map<String, String> getModuleDisplayNameMap() {
        Map<String, String> displayNameModuleMap = new HashMap<>();
        for (ModuleDefinition moduleDefinition : Optional.ofNullable(moduleList).orElse(Collections.emptyList())) {
            String displayName = moduleDefinition.getDisplayName();
            if (null == displayName) {
                continue;
            }
            displayNameModuleMap.put(moduleDefinition.getModule(), displayName);
        }
        return displayNameModuleMap;
    }

    public Map<String, String> getModuleNameMap() {
        Map<String, String> displayNameModuleMap = new HashMap<>();
        for (ModuleDefinition moduleDefinition : Optional.ofNullable(moduleList).orElse(Collections.emptyList())) {
            String moduleName = moduleDefinition.getName();
            if (null == moduleName) {
                continue;
            }
            displayNameModuleMap.put(moduleDefinition.getModule(), moduleName);
        }
        return displayNameModuleMap;
    }

    public Map<String, String> getModuleNameModuleMap() {
        Map<String, String> displayNameModuleMap = new HashMap<>();
        for (ModuleDefinition moduleDefinition : Optional.ofNullable(moduleList).orElse(Collections.emptyList())) {
            String moduleName = moduleDefinition.getName();
            if (null == moduleName) {
                continue;
            }
            displayNameModuleMap.put(moduleName, moduleDefinition.getModule());
        }
        return displayNameModuleMap;
    }
}
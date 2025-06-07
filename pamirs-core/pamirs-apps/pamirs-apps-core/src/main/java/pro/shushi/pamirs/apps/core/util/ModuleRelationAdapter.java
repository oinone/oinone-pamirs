package pro.shushi.pamirs.apps.core.util;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDependency;
import pro.shushi.pamirs.meta.domain.module.ModuleExclusion;
import pro.shushi.pamirs.meta.domain.module.ModuleUpstream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用依赖和互斥关系,当前未保存关联表,因此先通过主表冗余字段兼容
 *
 * @author drome
 * @date 2022/4/26下午4:38
 */
@Component
@Deprecated
public class ModuleRelationAdapter {

    /**
     * 根据指定的模块,转换模块依赖和互斥关系
     */
    public <T extends ModuleDefinition> T convertModuleRelation(T module) {
        if (CollectionUtils.isNotEmpty(module.getModuleDependencies())) {
            module.setModuleDependencyList(
                    module.getModuleDependencies().stream()
                            .map(_dm -> {
                                ModuleDependency moduleDependency = new ModuleDependency();
                                moduleDependency.setModule(module.getModule());
                                moduleDependency.setDependencyModule(_dm);
                                return moduleDependency;
                            })
                            .collect(Collectors.toList())
            );
        }
        if (CollectionUtils.isNotEmpty(module.getModuleUpstreams())) {
            module.setModuleUpstreamList(
                    module.getModuleUpstreams().stream()
                            .map(v -> {
                                ModuleUpstream moduleUpstream = new ModuleUpstream();
                                moduleUpstream.setModule(module.getModule());
                                moduleUpstream.setUpstreamModule(v);
                                return moduleUpstream;
                            })
                            .collect(Collectors.toList())
            );
        }
        if (CollectionUtils.isNotEmpty(module.getModuleExclusions())) {
            module.setModuleExclusionList(
                    module.getModuleExclusions().stream()
                            .map(_em -> {
                                ModuleExclusion moduleExclusion = new ModuleExclusion();
                                moduleExclusion.setModule(module.getModule());
                                moduleExclusion.setExcludeModule(_em);
                                return moduleExclusion;
                            })
                            .collect(Collectors.toList())
            );
        }
        return module;
    }

    public <T extends AppsManagementModule> T convertAppsModuleRelation(T module) {
        module = convertModuleRelation(module);

        Set<String> modules = new HashSet<>();
        List<String> moduleDependencies = module.getModuleDependencies();
        if (CollectionUtils.isNotEmpty(moduleDependencies)) {
            modules.addAll(moduleDependencies);
            moduleDependencies = moduleDependencies.stream().filter(v -> !ModuleConstants.MODULE_BASE.equals(v)).collect(Collectors.toList());
            module.setModuleDependencies(moduleDependencies);
        }
        if (CollectionUtils.isNotEmpty(module.getModuleUpstreams())) {
            modules.addAll(module.getModuleUpstreams());
        }
        if (CollectionUtils.isNotEmpty(module.getModuleExclusions())) {
            modules.addAll(module.getModuleExclusions());
        }
        if (CollectionUtils.isEmpty(modules)) {
            return module;
        }
        List<AppsManagementModule> appsManagementModules = Models.origin().queryListByWrapper(
                Pops.<AppsManagementModule>lambdaQuery()
                        .from(AppsManagementModule.MODEL_MODEL)
                        .in(AppsManagementModule::getModule, modules)
        );
        if (CollectionUtils.isEmpty(appsManagementModules)) {
            return module;
        }
        Map<String, AppsManagementModule> moduleMap = appsManagementModules.stream().collect(Collectors.toMap(AppsManagementModule::getModule, i -> i));

        if (CollectionUtils.isNotEmpty(moduleDependencies)) {
            module.setAppsModuleDependencyList(
                    moduleDependencies
                            .stream()
                            .filter(v -> !ModuleConstants.MODULE_BASE.equals(v))
                            .map(moduleMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }
        if (CollectionUtils.isNotEmpty(module.getModuleUpstreams())) {
            module.setAppsModuleUpstreamList(
                    module.getModuleUpstreams()
                            .stream()
                            .map(moduleMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }
        if (CollectionUtils.isNotEmpty(module.getModuleExclusions())) {
            module.setAppsModuleExclusionList(
                    module.getModuleExclusions()
                            .stream()
                            .map(moduleMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }

        return module;
    }

    /**
     * 根据模块列表,得到所有的模块依赖关系
     *
     * @param moduleList
     * @return
     */
    public <T extends ModuleDefinition> List<T> convertModulesRelation(List<T> moduleList) {
        if (CollectionUtils.isEmpty(moduleList)) {
            return moduleList;
        }
        return moduleList.stream().map(this::convertModuleRelation).collect(Collectors.toList());
    }
}

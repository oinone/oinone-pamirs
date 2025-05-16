package pro.shushi.pamirs.framework.configure.annotation.core;

import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.*;

/**
 * 模块依赖获取器
 * <p>
 * 2020/10/16 5:06 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModuleDependencyResolver {

    public static void fetchModuleDependencyPackage(Set<String> collectSortedModules,
                                                    Map<String, String[]> dependentPackagePrefix,
                                                    Map<String/*module*/, ModuleDefinition> pamirsModuleMap,
                                                    ModuleDefinition pamirsModule) {
        recursion(new HashSet<>(), pamirsModuleMap, pamirsModule, currentModule -> {
            if (null != currentModule.getDependentPackagePrefix()) {
                dependentPackagePrefix.putAll(currentModule.getDependentPackagePrefix());
            }
            return true;
        }, dependentModule -> {
            collectSortedModules.add(dependentModule.getModule());
            String[] dependentModulePackages = dependentModule.getPackagePrefix();
            dependentPackagePrefix.putIfAbsent(dependentModule.getModule(), dependentModulePackages);
            return true;
        }, null);
    }

    public static List<String> sortModulesByDependent(Map<String/*module*/, ModuleDefinition> pamirsModuleMap,
                                                      Set<String> moduleSet) {
        List<String> collectSortedModules = new ArrayList<>(moduleSet.size());
        Set<String> completedModuleSet = new HashSet<>();
        for (String module : moduleSet) {
            ModuleDefinition moduleDefinition = pamirsModuleMap.get(module);
            recursion(completedModuleSet, pamirsModuleMap, moduleDefinition, null, null,
                    currentModule -> {
                        String moduleModule = currentModule.getModule();
                        if (moduleSet.contains(moduleModule)) {
                            collectSortedModules.add(moduleModule);
                        }
                        return true;
                    });
        }
        return collectSortedModules;
    }

    public static boolean recursion(Set<String> completedModules,
                                    Map<String/*module*/, ModuleDefinition> pamirsModuleMap,
                                    ModuleDefinition pamirsModule,
                                    Function<ModuleDefinition, Boolean> beforeConsumer,
                                    Function<ModuleDefinition, Boolean> dependentConsumer,
                                    Function<ModuleDefinition, Boolean> afterConsumer) {
        String module = pamirsModule.getModule();
        if (completedModules.contains(module)) {
            return false;
        }
        completedModules.add(module);
        if (null != beforeConsumer) {
            if (!beforeConsumer.apply(pamirsModule)) {
                return false;
            }
        }
        if (null != pamirsModule.getModuleDependencies()) {
            for (String dependentModuleModule : pamirsModule.getModuleDependencies()) {
                ModuleDefinition dependentModule = pamirsModuleMap.get(dependentModuleModule);
                if (null == dependentModule) {
                    throw PamirsException.construct(BASE_DEPENDENT_MODULE_IS_NOT_EXISTS_ERROR)
                            .appendMsg("currentModule: " + pamirsModule.getModule() +
                                    ", dependentModule: " + dependentModuleModule +
                                    ", dependencies: " + pamirsModule.getModuleDependencies()).errThrow();
                }
                if (!recursion(completedModules, pamirsModuleMap, dependentModule, beforeConsumer, dependentConsumer, afterConsumer)) {
                    continue;
                }
                if (null != dependentConsumer) {
                    if (!dependentConsumer.apply(dependentModule)) {
                        return false;
                    }
                }
            }
        }
        if (null != afterConsumer) {
            return afterConsumer.apply(pamirsModule);
        }
        return true;
    }

    public static void consumeModuleDependency(Map<String/*module*/, ModuleDefinition> moduleInfoMap,
                                               ModuleDefinition currentModule,
                                               Function<ModuleDefinition, Boolean> canContinue,
                                               BiConsumer<ModuleDefinition, ModuleDefinition> consumer) {
        if (null != currentModule.getModuleDependencies()) {
            for (String dependentModuleModule : currentModule.getModuleDependencies()) {
                ModuleDefinition dependentModule = moduleInfoMap.get(dependentModuleModule);
                if (null == dependentModule) {
                    throw PamirsException.construct(BASE_CONSUME_DEPENDENT_MODULE_IS_NOT_EXISTS_ERROR)
                            .appendMsg("currentModule: " + currentModule.getModule() +
                                    ", dependentModule: " + dependentModuleModule +
                                    ", dependencies: " + currentModule.getModuleDependencies()).errThrow();
                }
                if (canContinue.apply(dependentModule)) {
                    consumeModuleDependency(moduleInfoMap, dependentModule, canContinue, consumer);
                }
                consumer.accept(currentModule, dependentModule);
            }
        }
    }

    public static void consumeModuleExclusion(Map<String/*module*/, ModuleDefinition> moduleInfoMap,
                                              ModuleDefinition currentModule,
                                              BiConsumer<ModuleDefinition, ModuleDefinition> consumer) {
        if (null != currentModule.getModuleExclusions()) {
            for (String exclusionModuleModule : currentModule.getModuleExclusions()) {
                ModuleDefinition exclusionModule = moduleInfoMap.get(exclusionModuleModule);
                if (null == exclusionModule) {
                    throw PamirsException.construct(BASE_CONSUME_EXCLUSION_MODULE_IS_NOT_EXISTS_ERROR)
                            .appendMsg("currentModule: " + currentModule.getModule() +
                                    ", exclusionModule: " + exclusionModuleModule +
                                    ", exclusions: " + currentModule.getModuleExclusions()).errThrow();
                }
                consumer.accept(currentModule, exclusionModule);
            }
        }
    }

}

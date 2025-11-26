package pro.shushi.pamirs.framework.configure.annotation.core;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 模块搜索器
 * <p>
 * 2020/10/16 10:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class ModuleResolver {

    private static final Map<String, ModuleDefinition> jarModuleMap = new ConcurrentHashMap<>();

    public Map<String/*module*/, ModuleDefinition> resolve() {
        if (MapUtils.isNotEmpty(jarModuleMap)) {
            return jarModuleMap;
        }
        Map<String, PamirsModule> pamirsModuleBeanMap = BeanDefinitionUtils.getBeansOfType(PamirsModule.class);
        // 计算启动模块
        for (PamirsModule pamirsModule : Objects.requireNonNull(pamirsModuleBeanMap).values()) {
            Module moduleAnnotation = AnnotationUtils.getAnnotation(pamirsModule.getClass(), Module.class);
            Module.Advanced moduleAdvancedAnnotation = AnnotationUtils.getAnnotation(pamirsModule.getClass(), Module.Advanced.class);
            @SuppressWarnings("unchecked")
            String module = Spider.getExtension(ModelReflectSigner.class, ModuleDefinition.MODEL_MODEL).sign(null, pamirsModule.getClass());
            String moduleName = Optional.ofNullable(moduleAnnotation).map(Module::name).filter(StringUtils::isNotBlank).orElse(PStringUtils.dotName2ShortName(module));
            String version = Optional.ofNullable(moduleAnnotation).map(Module::version).orElse(null);
            List<String> moduleDependencies = Optional.ofNullable(moduleAnnotation).map(Module::dependencies).map(PStringUtils::trim).orElseGet(ArrayList::new);
            if (!ModuleConstants.MODULE_BASE.equals(module) && !moduleDependencies.contains(ModuleConstants.MODULE_BASE)) {
                moduleDependencies.add(0, ModuleConstants.MODULE_BASE);
            }
            List<String> moduleExclusions = Optional.ofNullable(moduleAnnotation).map(Module::exclusions).map(PStringUtils::trim).orElse(null);
            Class<? extends PamirsModule> moduleClazz = pamirsModule.getClass();
            String[] packagePrefix = pamirsModule.packagePrefix();
            Map<String, String[]> dependentPackagePrefix = pamirsModule.dependentPackagePrefix();
            if (StringUtils.isBlank(moduleName)) {
                continue;
            }

            ModuleDefinition moduleDefinition = new ModuleDefinition().setModule(module).setName(moduleName).setLatestVersion(version)
                    .setModuleDependencies(moduleDependencies)
                    .setModuleExclusions(moduleExclusions)
                    .setModuleClazz(moduleClazz).setPackagePrefix(packagePrefix)
                    .setDependentPackagePrefix(dependentPackagePrefix)
                    .setCore(Optional.ofNullable(moduleAdvancedAnnotation).map(Module.Advanced::core).orElse(false));
            if (null != moduleDefinition.getModuleDependencies()
                    && !moduleDefinition.getModuleDependencies().containsAll(pamirsModule.dependentPackagePrefix().keySet())) {
                throw PamirsException.construct(AnnotationExpEnumerate.BASE_ERROR_DEPENDENCY_PACKAGE_PREFIX_ERROR)
                        .appendMsg("module:" + module).errThrow();
            }
            jarModuleMap.put(module, moduleDefinition);
        }
        return jarModuleMap;
    }

    public List<ModuleDefinition> resolve(Set<String> runModules, Set<String> sortedModule, List<ModuleDefinition> moduleDefinitions) {
        Map<String, ModuleDefinition> moduleMap = moduleDefinitions.stream()
                .filter(v -> runModules.contains(v.getModule())).collect(Collectors.toMap(ModuleDefinition::getModule, v -> v));
        return sortedModule.stream().filter(moduleMap::containsKey).map(moduleMap::get).collect(Collectors.toList());
    }

}

package pro.shushi.pamirs.framework.configure.annotation.core;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FuseModuleResolver
 *
 * @author yakir on 2025/01/23 13:42.
 */
@Component
public class FuseModuleResolver {

    private static final Map<String, String> fuseMap = new ConcurrentHashMap<>();

    public Map<String/*module*/, String> resolve() {
        if (MapUtils.isNotEmpty(fuseMap)) {
            return fuseMap;
        }
        Map<String, PamirsModule> pamirsModuleBeanMap = BeanDefinitionUtils.getBeansOfType(PamirsModule.class);
        // 计算启动模块
        for (PamirsModule pamirsModule : Objects.requireNonNull(pamirsModuleBeanMap).values()) {
            Module.Fuse moduleFuseAnnotation = AnnotationUtils.getAnnotation(pamirsModule.getClass(), Module.Fuse.class);
            if (null == moduleFuseAnnotation) {
                continue;
            }
            Module moduleAnnotation = AnnotationUtils.getAnnotation(pamirsModule.getClass(), Module.class);

            @SuppressWarnings("unchecked")
            String module = Spider.getExtension(ModelReflectSigner.class, ModuleDefinition.MODEL_MODEL).sign(null, pamirsModule.getClass());
            String moduleName = Optional.ofNullable(moduleAnnotation).map(Module::name).filter(StringUtils::isNotBlank).orElse(PStringUtils.dotName2ShortName(module));
            fuseMap.put(module, moduleName);
        }
        return fuseMap;
    }

    public void clear() {
        fuseMap.clear();
    }
}

package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Optional;

/**
 * 模块签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@SPI.Service(ModuleDefinition.MODEL_MODEL)
public class ModuleReflectSigner implements ModelReflectSigner<ModuleDefinition, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        Module.module moduleModuleAnnotation = AnnotationUtils.getAnnotation(source, Module.module.class);
        return Optional.ofNullable(moduleModuleAnnotation).map(Module.module::value).orElse(source.getSimpleName());
    }

}

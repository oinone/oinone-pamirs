package pro.shushi.pamirs.boot.web.signer.reflect;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 模块签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@SPI.Service(UeModule.MODEL_MODEL)
public class UeModuleSigner implements ModelReflectSigner<UeModule, Class> {

    @SuppressWarnings("unchecked")
    @Override
    public String sign(MetaNames names, Class source) {
        return Spider.getExtension(ModelReflectSigner.class, ModuleDefinition.MODEL_MODEL).sign(names, source);
    }

}

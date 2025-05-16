package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.LayoutDefinition;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 布局签名器
 *
 * @version 1.0.0
 * date 2022/06/27
 */
@SPI.Service(LayoutDefinition.MODEL_MODEL)
public class LayoutDefinitionSigner implements ModelSigner<LayoutDefinition> {

    @Override
    public String sign(LayoutDefinition metaModelObject) {
        return metaModelObject.getName();
    }

}
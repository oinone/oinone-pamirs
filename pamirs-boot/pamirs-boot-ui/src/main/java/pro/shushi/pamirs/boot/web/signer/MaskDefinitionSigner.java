package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.MaskDefinition;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 母版签名器
 *
 * @author Adamancy Zhang at 16:02 on 2024-02-24
 */
@SPI.Service(MaskDefinition.MODEL_MODEL)
public class MaskDefinitionSigner implements ModelSigner<MaskDefinition> {

    @Override
    public String sign(MaskDefinition metaModelObject) {
        return metaModelObject.getName();
    }

}

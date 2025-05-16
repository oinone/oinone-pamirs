package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelCategory;

/**
 * ModelCategorySigner
 *
 * @author yakir on 2021/08/10 10:53.
 */
@SPI.Service(ModelCategory.MODEL_MODEL)
public class ModelCategorySigner implements ModelSigner<ModelCategory> {

    @Override
    public String sign(ModelCategory metaModelObject) {
        return metaModelObject.getName();
    }
}

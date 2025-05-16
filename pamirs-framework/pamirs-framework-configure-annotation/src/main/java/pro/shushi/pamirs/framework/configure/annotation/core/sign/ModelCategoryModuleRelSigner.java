package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelCategoryModuleRel;

/**
 * ModelCategoryModuleRelSigner
 *
 * @author yakir on 2021/08/10 10:56.
 */
@SPI.Service(ModelCategoryModuleRel.MODEL_MODEL)
public class ModelCategoryModuleRelSigner implements ModelSigner<ModelCategoryModuleRel> {

    @Override
    public String sign(ModelCategoryModuleRel metaModelObject) {
        return String.valueOf(metaModelObject.getCategoryId() + metaModelObject.getModuleId());
    }

}

package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.ViewCategoryModuleRel;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 视图分组签名器
 *
 * @author Adamancy Zhang at 14:24 on 2021-12-28
 */
@SPI.Service(ViewCategoryModuleRel.MODEL_MODEL)
public class ViewCategoryModuleRelSigner implements ModelSigner<ViewCategoryModuleRel> {

    @Override
    public String sign(ViewCategoryModuleRel metaModelObject) {
        return metaModelObject.getCategoryId() + CharacterConstants.SEPARATOR_DOT + metaModelObject.getModuleId();
    }
}

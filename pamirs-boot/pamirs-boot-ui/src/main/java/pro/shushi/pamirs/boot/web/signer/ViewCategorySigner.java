package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.ViewCategory;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 视图分组签名器
 *
 * @author Adamancy Zhang at 14:24 on 2021-12-28
 */
@SPI.Service(ViewCategory.MODEL_MODEL)
public class ViewCategorySigner implements ModelSigner<ViewCategory> {

    @Override
    public String sign(ViewCategory metaModelObject) {
        return metaModelObject.getName();
    }
}

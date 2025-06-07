package pro.shushi.pamirs.boot.web.signer;

import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(UeModel.MODEL_MODEL)
public class UeModelSigner implements ModelSigner<UeModel> {

    @Override
    public String sign(UeModel metaModelObject) {
        return metaModelObject.getModel();
    }

}

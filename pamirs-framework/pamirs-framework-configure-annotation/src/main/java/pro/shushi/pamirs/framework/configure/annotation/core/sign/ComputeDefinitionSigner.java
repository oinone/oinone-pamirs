package pro.shushi.pamirs.framework.configure.annotation.core.sign;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;

/**
 * 校验函数定义签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(ComputeDefinition.MODEL_MODEL)
public class ComputeDefinitionSigner implements ModelSigner<ComputeDefinition> {

    @Override
    public String sign(ComputeDefinition metaModelObject) {
        return ComputeDefinition.sign(metaModelObject.getType(), metaModelObject.getModel(),
                metaModelObject.getLocation(), metaModelObject.getFun());
    }

}

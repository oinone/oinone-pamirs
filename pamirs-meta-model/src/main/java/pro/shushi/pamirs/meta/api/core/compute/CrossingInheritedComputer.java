package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 跨模型继承生成元数据计算接口
 * <p>
 * 2020/11/30 10:20 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface CrossingInheritedComputer {

    /**
     * 计算
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void compute(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

}

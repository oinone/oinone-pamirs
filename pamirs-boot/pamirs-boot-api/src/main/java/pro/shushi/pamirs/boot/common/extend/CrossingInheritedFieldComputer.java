package pro.shushi.pamirs.boot.common.extend;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.compute.CrossingInheritedComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import javax.annotation.Resource;

import static pro.shushi.pamirs.boot.common.extend.CrossingInheritedFieldComputer.SPI_NAME;

/**
 * 跨模型生成字段计算
 * 2020/11/30 10:24 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service(SPI_NAME)
@Component
public class CrossingInheritedFieldComputer implements CrossingInheritedComputer {

    public final static String SPI_NAME = "CROSSING_INHERITED_FIELD_COMPUTER";

    @Resource
    private InheritedProcessor inheritedProcessor;

    @Override
    public void compute(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum) {
        // 重新设置继承自跨模型继承字段的字段配置（跨模型继承的一对多、多对多关联关系配置）
        inheritedProcessor.dealCrossingInheritedField(meta, modelDefinition, superModel, systemSourceEnum);
    }

}

package pro.shushi.pamirs.meta.api.core.compute.template;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;

/**
 * 模型定义计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DefinitionComputer extends CommonApi {

    /**
     * 自定义计算
     *
     * @param context        上下文
     * @param meta           元数据
     * @param extendCompute  是否执行扩展计算
     * @param definitionList 模型定义列表
     * @param modelComputer  模型计算
     * @param fieldComputers 字段计算器列表
     * @return 返回值
     */
    @SuppressWarnings("rawtypes")
    Result<Void> compute(ComputeContext context, Meta meta, boolean extendCompute,
                         List<ModelDefinition> definitionList, ModelComputer modelComputer, FieldComputer... fieldComputers);

}

package pro.shushi.pamirs.meta.api.core.compute.template;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 生命周期数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface LifeDataComputer<C, T> extends CommonApi {

    /**
     * 自定义计算
     *
     * @param model          模型编码
     * @param context        上下文
     * @param config         配置
     * @param data           数据
     * @param modelComputer  模型计算
     * @param fieldComputers 字段计算器列表
     * @return 计算结果
     */
    @SuppressWarnings("rawtypes")
    Result<Void> compute(String model, ComputeContext context, C config, T data, ModelComputer modelComputer, FieldComputer... fieldComputers);

}

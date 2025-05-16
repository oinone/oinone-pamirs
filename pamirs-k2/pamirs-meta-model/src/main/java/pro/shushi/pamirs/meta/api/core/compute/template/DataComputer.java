package pro.shushi.pamirs.meta.api.core.compute.template;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataComputer<T> extends CommonApi {

    /**
     * 自定义计算
     *
     * @param context        上下文
     * @param model          模型编码
     * @param data           数据
     * @param modelComputer  模型计算
     * @param fieldComputers 字段计算器列表
     * @return 计算结果
     */
    @SuppressWarnings("rawtypes")
    Result<Void> compute(ComputeContext context, String model, T data, ModelComputer modelComputer, FieldComputer... fieldComputers);

}

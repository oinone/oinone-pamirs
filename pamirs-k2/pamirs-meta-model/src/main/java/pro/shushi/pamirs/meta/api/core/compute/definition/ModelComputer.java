package pro.shushi.pamirs.meta.api.core.compute.definition;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

/**
 * 模型计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI
public interface ModelComputer<D, T> extends CommonApi {

    /**
     * 模型计算
     *
     * @param context        上下文
     * @param meta           元数据
     * @param model          模型编码
     * @param data           实体数据
     * @param computeContext 计算器上下文
     * @return 返回值
     */
    Result<Void> compute(ComputeContext context, D meta, String model, T data, Map<String, Object> computeContext);

}

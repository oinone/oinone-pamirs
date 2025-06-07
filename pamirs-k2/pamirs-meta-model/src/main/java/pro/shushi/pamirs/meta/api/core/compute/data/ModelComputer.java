package pro.shushi.pamirs.meta.api.core.compute.data;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI
public interface ModelComputer<T> extends CommonApi {

    /**
     * 模型计算
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    实体数据
     * @return 计算结果
     */
    Result<Void> compute(ComputeContext context, String model, T data);

}

package pro.shushi.pamirs.meta.api.core.compute.model;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 模型计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface ModelComputer<D, T> extends CommonApi {

    /**
     * 模型计算
     *
     * @param meta 元数据
     * @param model 模型编码
     * @param data 实体数据
     * @return
     */
    Result<Void> compute(D meta, String model, T data);

}

package pro.shushi.pamirs.framework.orm.json.api;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 数据计算
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface DataComputeApi extends CommonApi {

    /**
     * 计算
     *
     * @param model    模型编码
     * @param obj      数据
     * @param features 特性
     * @return 计算结果
     */
    Object run(String model, Object obj, int features);

}
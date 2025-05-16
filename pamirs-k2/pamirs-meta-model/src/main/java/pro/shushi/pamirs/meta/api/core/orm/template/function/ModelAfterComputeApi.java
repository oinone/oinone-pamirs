package pro.shushi.pamirs.meta.api.core.orm.template.function;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 模型后置计算
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModelAfterComputeApi<T> extends CommonApi {

    /**
     * 对象计算
     *
     * @param model 模型编码
     * @param obj   待计算对象
     * @return 计算结果
     */
    T after(String model, T obj);

}
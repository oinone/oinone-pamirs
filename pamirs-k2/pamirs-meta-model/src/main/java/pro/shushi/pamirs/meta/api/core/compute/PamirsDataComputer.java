package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

/**
 * pamirs数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface PamirsDataComputer<T> extends CommonApi {

    /**
     * 计算模型数据
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 计算结果
     */
    Result<Void> computeModel(ComputeContext context, String model, T data);

    /**
     * 计算模型数据（在模块生命周期中）
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 计算结果
     */
    Result<Void> computeModelInLifecycle(ComputeContext context, String model, T data);

    /**
     * 计算关系字段
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 计算结果
     */
    Result<Void> computeRelationField(ComputeContext context, String model, T data);

    /**
     * 模型与字段约束
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 校验结果
     */
    Result<Void> check(ComputeContext context, String model, Object data);

    /**
     * 模型约束
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 校验结果
     */
    Result<Void> checkModel(ComputeContext context, String model, Object data);

    /**
     * 字段约束
     *
     * @param context 上下文
     * @param model   模型编码
     * @param data    数据
     * @return 校验结果
     */
    Result<Void> checkField(ComputeContext context, String model, Object data);

    /**
     * 字段约束
     *
     * @param context 上下文
     * @param field   字段配置
     * @param data    数据
     * @return 校验结果
     */
    Result<Void> checkField(ComputeContext context, ModelFieldConfig field, Object data);

}

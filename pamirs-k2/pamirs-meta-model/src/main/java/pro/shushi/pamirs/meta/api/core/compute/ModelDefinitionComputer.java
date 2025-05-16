package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.List;
import java.util.Set;

/**
 * 模型定义计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModelDefinitionComputer extends CommonApi {

    /**
     * 计算
     * <p>
     * 计算默认值、计算字段、反转、序列生成、约束函数和表达式检查
     * 处理继承、补充关系字段、多对多关联模型
     *
     * @param context            上下文
     * @param meta               元数据
     * @param completedModuleSet 已完成计算模块
     * @return 返回值
     */
    Result<Void> compute(ComputeContext context, Meta meta, Set<String/*module*/> completedModuleSet);

    /**
     * 计算
     * <p>
     * 不clear session
     *
     * @param context            上下文
     * @param meta               元数据
     * @param completedModuleSet 已完成计算模块
     * @return 返回值
     */
    Result<Void> computeWithoutClearSession(ComputeContext context, Meta meta, Set<String/*module*/> completedModuleSet);

    /**
     * 计算
     *
     * @param context  上下文
     * @param metaList 元数据列表
     * @return 返回结果
     */
    Result<Void> compute(ComputeContext context, List<Meta> metaList);

    /**
     * 计算模型
     *
     * @param context          上下文
     * @param meta             元数据
     * @param modelDefinitions 模型定义
     * @return 计算结果
     */
    Result<Void> computeModel(ComputeContext context, Meta meta, List<ModelDefinition> modelDefinitions);

    /**
     * 计算字段定义（计算默认值、校验）
     *
     * @param context   上下文
     * @param fieldList 字段列表
     * @return 返回值
     */
    @SuppressWarnings("UnusedReturnValue")
    Result<Void> computeField(ComputeContext context, Meta meta, List<ModelField> fieldList);

    /**
     * 计算字段定义默认值
     *
     * @param context   上下文
     * @param fieldList 字段列表
     * @return 返回值
     */
    @SuppressWarnings("UnusedReturnValue")
    Result<Void> constructField(ComputeContext context, Meta meta, List<ModelField> fieldList);

    /**
     * 校验字段定义
     *
     * @param context   上下文
     * @param fieldList 字段列表
     * @return 返回值
     */
    @SuppressWarnings("UnusedReturnValue")
    Result<Void> checkField(ComputeContext context, Meta meta, List<ModelField> fieldList);

}

package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Set;

/**
 * 元数据定义计算器
 *
 * @author Adamancy Zhang at 19:03 on 2025-02-21
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaDataModelComputer {

    /**
     * 计算
     * <p>
     * 计算默认值、计算字段、反转、序列生成、约束函数和表达式检查
     * 处理继承、补充关系字段、多对多关联模型
     *
     * @param context            上下文
     * @param metaList           元数据集
     * @param completedModuleSet 已完成计算模块
     */
    void compute(ComputeContext context, List<Meta> metaList, Set<String/*module*/> completedModuleSet);

}

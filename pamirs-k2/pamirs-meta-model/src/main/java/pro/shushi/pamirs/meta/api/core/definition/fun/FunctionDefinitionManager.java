package pro.shushi.pamirs.meta.api.core.definition.fun;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 函数定义管理接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
public interface FunctionDefinitionManager extends CommonApi {

    /**
     * 将函数定义添加到函数定义列表
     * <p>
     * 判断函数定义是否符合加入到模型的函数定义列表
     * <p>
     * 1.当函数是数据管理器函数，而模型（例如临时模型）不使用默认数据管理器函数，不加入到模型
     *
     * @param modelConfig  模型
     * @param functionList 函数定义列表
     * @param function     函数
     */
    default void metaAdd(ModelConfig modelConfig, List<Function> functionList, Function function) {
        // 模型是否使用默认数据管理器
        Boolean dataManagedModel = modelConfig.isDataManager();
        // 是否是数据管理器函数
        Boolean dataManagedFunction = function.isDataManager();
        if (!dataManagedModel && dataManagedFunction && !ModelTypeEnum.ABSTRACT.equals(modelConfig.getType())) {
            return;
        }
        functionList.add(function);
    }

    /**
     * 将函数定义添加到前端元数据函数定义列表
     * <p>
     * 判断函数定义是否对前端开放
     * <p>
     * 1.处理函数开放级别
     *
     * @param modelConfig 模型
     * @param function    函数
     */
    default boolean canClientInvoke(ModelConfig modelConfig, Function function) {
        return true;
    }

}

package pro.shushi.pamirs.meta.api.core.definition.model;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.util.List;
import java.util.Map;

/**
 * 模型定义管理接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
public interface ModelDefinitionManager extends CommonApi {

    /**
     * 为前端处理继承关系元数据
     * <p>
     * 1.扩展继承只提供一个模型，去掉扩展继承（同表）子类，只保留祖先存储模型给前端；祖先存储模型拥有所有扩展继承子类的字段（已在注解转换时计算）
     * 2.同表继承，各自独立，在模型上无需处理
     *
     * @param modelConfig  模型
     * @param functionList 函数定义列表
     * @param function     函数
     */
    void dealInheritedForClient(ModelConfig modelConfig, List<Function> functionList, Function function);

    /**
     * 从模型数据中获取非空字段数量值
     *
     * @param model 模型编码
     * @param data  模型数据
     * @return 非空字段数量
     */
    int countNonEmptyModelFieldSizeFromDMap(String model, Map<String, Object> data);

}

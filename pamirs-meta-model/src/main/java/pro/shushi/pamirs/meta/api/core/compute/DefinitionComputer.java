package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.model.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.model.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;

/**
 * 模型定义计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface DefinitionComputer extends CommonApi {

    /**
     * 自定义计算
     *
     * @param metaData 元数据
     * @param definitionList 模型定义列表
     * @param modelComputer 模型计算
     * @param fieldComputers 字段计算器列表
     * @return
     */
    Result<Void> compute(MetaData metaData, List<ModelDefinition> definitionList, ModelComputer modelComputer, FieldComputer... fieldComputers);

}

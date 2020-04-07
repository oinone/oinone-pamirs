package pro.shushi.pamirs.meta.api.core.configure;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;

import java.util.List;
import java.util.Map;

/**
 * 元数据引擎接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface DefinitionConfigurer extends CommonApi {

    /**
     * 读取模块所有模型定义
     *
     * @param includeModules 包含模块编码列表
     * @param excludeModules 排除模块编码列表
     * @return
     */
    Result<List<Meta>> extractDefinition(List<String> includeModules, List<String> excludeModules);

    /**
     * 从模型定义中获取模型配置
     *
     * @param metaDataList
     * @return
     */
    Result<Map<String/*model*/, ModelConfig>> fetchConfig(List<MetaData> metaDataList);

    /**
     * 从模型配置中获取Model和Field的模型配置
     *
     * @param modelConfigMap
     * @return
     */
    Result<Map<String/*model*/, ModelConfig>> fetchMeta(Map<String/*model*/, ModelConfig> modelConfigMap);

}

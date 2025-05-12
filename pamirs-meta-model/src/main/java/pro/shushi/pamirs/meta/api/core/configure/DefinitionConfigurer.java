package pro.shushi.pamirs.meta.api.core.configure;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @return 元数据列表
     */
    Result<List<Meta>> extractDefinition(final Set<String> includeModules, final Set<String> excludeModules);

    /**
     * 读取模块所有模型定义
     *
     * @param installMeta     安装元数据
     * @param includeModules  包含模块编码列表
     * @param excludeModules  排除模块编码列表
     * @param moduleInfoMap   所有模块信息
     * @param updateModuleMap 更新模块
     * @param loadModuleMap   重启模块
     * @return 元数据列表
     */
    Result<List<Meta>> extractDefinition(final boolean installMeta,
                                         final Set<String> includeModules, final Set<String> excludeModules,
                                         final Map<String, ModuleDefinition> moduleInfoMap,
                                         final Map<String, MetaData> updateModuleMap,
                                         final Map<String, MetaData> loadModuleMap);

}

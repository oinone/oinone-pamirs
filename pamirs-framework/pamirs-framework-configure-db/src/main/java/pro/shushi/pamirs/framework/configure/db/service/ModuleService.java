package pro.shushi.pamirs.framework.configure.db.service;

import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 模块服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
public interface ModuleService extends CommonApi {

    /**
     * 自定义模块获取
     *
     * @param appendModelMap 追加临时模型配置
     * @param moduleQuery    模块查询条件
     * @param preAction      前置动作
     * @return 模块
     */
    Map<String, ModuleDefinition> fetchModuleMapFromDB(Map<String/*model*/, String/*simulate model*/> appendModelMap,
                                                       QueryWrapper<ModuleDefinition> moduleQuery,
                                                       Consumer<ModelDefinition> preAction);

}

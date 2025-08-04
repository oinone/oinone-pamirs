package pro.shushi.pamirs.framework.configure.db.service.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.configure.db.service.ModuleService;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 模块服务实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
@Component
public class DefaultModuleService implements ModuleService {

    @Resource
    private MetaSimulateService metaSimulateService;

    @Override
    public Map<String, ModuleDefinition> fetchModuleMapFromDB(Map<String/*model*/, String/*simulate model*/> modelMap,
                                                              QueryWrapper<ModuleDefinition> moduleQuery,
                                                              Consumer<ModelDefinition> preAction) {
        // 计算需要安装或者升级的模块
        Map<String, ModuleDefinition> dbModuleMap = new HashMap<>();
        // 临时构造模块的模型定义
        MetaService.get().prepareModels(modelMap);
        // 查询模块数据
        metaSimulateService.transientStaticExecuteWithoutResult(modelMap, () -> {
            ModelDefinition modulesDefinition = PamirsSession.getContext().getModelConfig(ModuleDefinition.MODEL_MODEL).getModelDefinition();
            String dsKey = DsApi.get().baseDsKey(ModuleDefinition.MODEL_MODEL);
            String moduleDefinitionTableName = DataPrefixManager
                    .tablePrefix(ModuleConstants.MODULE_BASE, ModuleDefinition.MODEL_MODEL, ModuleDefinition.TABLE_NAME);
            if (Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, moduleDefinitionTableName)) {
                // 如果表结构有变化，修改表结构
                preAction.accept(modulesDefinition);
                // 查询模块信息
                List<ModuleDefinition> existModules = Models.origin().queryListByWrapper(moduleQuery);
                for (ModuleDefinition module : existModules) {
                    // FIXME: zbh 20250227 此处修复之前apps创建的模块没有状态，导致需要重启两次的问题。在7.0.0后可移除此段代码
                    if (module.getState() == null) {
                        module.setState(ModuleStateEnum.UNINSTALLED);
                    }
                    String installedModule = module.getModule();
                    dbModuleMap.put(installedModule, module);
                }
            } else {
                // 如果模块表不存在，构建表
                preAction.accept(modulesDefinition);
            }
        });
        return dbModuleMap;
    }

}

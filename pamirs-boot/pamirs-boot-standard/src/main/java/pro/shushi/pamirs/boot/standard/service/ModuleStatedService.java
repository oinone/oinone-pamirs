package pro.shushi.pamirs.boot.standard.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.framework.configure.db.mapper.ModuleMapper;
import pro.shushi.pamirs.framework.configure.db.model.ModuleDefinitionStatic;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块安装、升级修改模块完成状态
 * <p>
 * 2020/9/24 4:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
public class ModuleStatedService implements LifecycleCompletedInit {

    @Resource
    private ModuleMapper moduleMapper;

    @Override
    public void process(AppLifecycleCommand command,
                        List<ModuleDefinition> installModules,
                        List<ModuleDefinition> upgradeModules,
                        List<ModuleDefinition> reloadModules) {
        if (0 == installModules.size() && 0 == upgradeModules.size()) {
            return;
        }
        List<ModuleDefinitionStatic> updateModules = new ArrayList<>();
        if (!InstallEnum.READONLY.equals(command.getInstallEnum())) {
            for (ModuleDefinition installModule : installModules) {
                String module = installModule.getModule();
                ModuleDefinition moduleDefinitionCache = PamirsSession.getContext().getModule(module);
                if (moduleDefinitionCache != null && !ModuleStateEnum.INSTALLED.equals(moduleDefinitionCache.getState())) {
                    moduleDefinitionCache.setState(ModuleStateEnum.INSTALLED);
                    PamirsSession.getContext().getModuleCache().put(module, moduleDefinitionCache);
                    updateModules.add((ModuleDefinitionStatic) new ModuleDefinitionStatic()
                            .setModule(module).setState(ModuleStateEnum.INSTALLED));
                }
            }
        }
        if (!UpgradeEnum.READONLY.equals(command.getUpgradeEnum())) {
            for (ModuleDefinition upgradeModule : upgradeModules) {
                String module = upgradeModule.getModule();
                ModuleDefinition moduleDefinitionCache = PamirsSession.getContext().getModule(module);
                if (moduleDefinitionCache != null && !ModuleStateEnum.INSTALLED.equals(moduleDefinitionCache.getState())) {
                    moduleDefinitionCache.setState(ModuleStateEnum.INSTALLED);
                    PamirsSession.getContext().getModuleCache().put(module, moduleDefinitionCache);
                    updateModules.add((ModuleDefinitionStatic) new ModuleDefinitionStatic()
                            .setModule(module).setState(ModuleStateEnum.INSTALLED));
                }
            }
        }
        // 判断是否注册模块信息
        boolean registerModule = command.getOptions().isUpdateModule();
        boolean registerMeta = command.getOptions().isUpdateMeta();
        if (!registerModule && !registerMeta) {
            return;
        }
        if (!CollectionUtils.isEmpty(updateModules)) {
            moduleMapper.updateBatchByUniqueKey(updateModules);
        }
    }

}

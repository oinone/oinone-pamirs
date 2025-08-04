package pro.shushi.pamirs.boot.standard.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.boot.common.api.init.LifecycleBeginInit;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModelPrepareApi;
import pro.shushi.pamirs.framework.configure.db.service.MetaService;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 模块安装、升级修改模块开始状态
 * <p>
 * 2020/9/24 4:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
public class ModuleStatingService implements LifecycleBeginInit {

    @Resource
    private MetaSimulateService metaSimulateService;

    @Override
    public void process(AppLifecycleCommand command, Set<String> runModules,
                        List<ModuleDefinition> installModules,
                        List<ModuleDefinition> upgradeModules,
                        List<ModuleDefinition> reloadModules) {
        if (0 == installModules.size() && 0 == upgradeModules.size()) {
            return;
        }
        // 判断是否注册模块信息
        boolean registerModule = command.getOptions().isUpdateModule();
        boolean registerMeta = command.getOptions().isUpdateMeta();
        if (!registerModule && !registerMeta) {
            return;
        }
        List<ModuleDefinition> insertList = new ArrayList<>();
        List<ModuleDefinition> updateList = new ArrayList<>();
        if (!InstallEnum.READONLY.equals(command.getInstallEnum())) {
            for (ModuleDefinition installModule : installModules) {
                if (!runModules.contains(installModule.getModule())) {
                    continue;
                }
                ModuleStateEnum state = !registerMeta ? ModuleStateEnum.INSTALLED : ModuleStateEnum.TOINSTALL;
                installModule.setState(state);
                if (null == installModule.getId()) {
                    insertList.add(installModule);
                } else {
                    updateList.add(installModule);
                }
            }
        }
        if (!UpgradeEnum.READONLY.equals(command.getUpgradeEnum())) {
            for (ModuleDefinition upgradeModule : upgradeModules) {
                if (!runModules.contains(upgradeModule.getModule())) {
                    continue;
                }
                ModuleStateEnum state = !registerMeta ? ModuleStateEnum.INSTALLED : ModuleStateEnum.TOUPGRADE;
                upgradeModule.setState(state);
                if (null == upgradeModule.getId()) {
                    insertList.add(upgradeModule);
                } else {
                    updateList.add(upgradeModule);
                }
            }
        }
        Map<String/*model*/, String/*simulate model*/> modelMap = new HashMap<>();
        Spider.getDefaultExtension(BootModelPrepareApi.class).prepare(modelMap);
        MetaService.get().prepareModels(modelMap);
        metaSimulateService.transientStaticExecuteWithoutResult(modelMap, () -> {
            if (!CollectionUtils.isEmpty(insertList)) {
                Models.origin().createBatch(insertList);
            }
            if (!CollectionUtils.isEmpty(updateList)) {
                Models.origin().updateBatch(updateList);
            }
        });
    }

}

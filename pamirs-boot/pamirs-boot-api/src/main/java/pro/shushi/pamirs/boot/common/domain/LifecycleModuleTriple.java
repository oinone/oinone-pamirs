package pro.shushi.pamirs.boot.common.domain;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

/**
 * 生命周期模块三元组
 * <p>
 * 2020/8/27 7:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class LifecycleModuleTriple {

    private List<ModuleDefinition> installModules;
    private List<ModuleDefinition> upgradeModules;
    private List<ModuleDefinition> reloadModules;

}

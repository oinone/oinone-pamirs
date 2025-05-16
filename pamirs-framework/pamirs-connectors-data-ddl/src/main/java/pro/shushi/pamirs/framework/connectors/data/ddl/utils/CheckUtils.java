package pro.shushi.pamirs.framework.connectors.data.ddl.utils;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import static pro.shushi.pamirs.meta.enmu.ModuleStateEnum.UNINSTALLABLE;
import static pro.shushi.pamirs.meta.enmu.ModuleStateEnum.UNINSTALLED;

/**
 * 校验工具类
 * <p>
 * 2021/2/2 10:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CheckUtils {

    public static boolean isValidMeta(String module, String registerModule) {
        if (!module.equals(registerModule) && null != PamirsSession.getContext()) {
            ModuleDefinition registerModuleDefinition = PamirsSession.getContext().getModule(registerModule);
            return null != registerModuleDefinition
                    && !UNINSTALLABLE.equals(registerModuleDefinition.getState())
                    && !UNINSTALLED.equals(registerModuleDefinition.getState());
        }
        return true;
    }

}

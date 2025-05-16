package pro.shushi.pamirs.sys.setting.api;


import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.sys.setting.model.SysSettings;

/**
 * SysSettingsService
 *
 * @author yakir on 2022/10/26 15:10.
 */
@Fun(SysSettingsService.FUN_NAMESPACE)
public interface SysSettingsService {

    String FUN_NAMESPACE = "core.sys.setting.SysSettingsService";

    @Function
    SysSettings sysSettings();

}

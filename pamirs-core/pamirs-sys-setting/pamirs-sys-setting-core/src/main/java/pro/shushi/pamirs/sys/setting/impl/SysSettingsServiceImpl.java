package pro.shushi.pamirs.sys.setting.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.sys.setting.model.SysSettings;

/**
 * BcSysSettingsServiceImpl
 *
 * @author yakir o n 2022/09/15 14:56.
 */
@Component
@Fun(SysSettingsService.FUN_NAMESPACE)
public class SysSettingsServiceImpl implements SysSettingsService {

    @Function
    @Override
    public SysSettings sysSettings() {
        SysSettings sysSettings = new SysSettings().queryById(1L);
        if (null == sysSettings.getInvitationCodeValidTime()) {
            sysSettings.setInvitationCodeValidTime(60 * 24 * 10);
        }
        if (null == sysSettings.getTrialDays()) {
            sysSettings.setTrialDays(7);
        }
        if (null == sysSettings.getNeedModifyInitialPassword()) {
            sysSettings.setNeedModifyInitialPassword(false);
        }
        return sysSettings;
    }
}

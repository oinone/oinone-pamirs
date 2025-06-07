package pro.shushi.pamirs.sys.setting.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.sys.setting.enmu.TenantDomainEnum;
import pro.shushi.pamirs.sys.setting.model.SysSettings;

/**
 * SysSettingsManager
 *
 * @author yakir on 2022/09/19 10:49.
 */
@Component
public class SysSettingsManager {

    @Autowired(required = false)
    private SysSettingsService sysSettingsService;

    private volatile SysSettings bcSysSettings;

    public boolean regTeamInvite() {
        check();
        Boolean regTeamInvite = bcSysSettings.getRegTeamInvite();
        if (null == regTeamInvite) {
            return false;
        }
        return regTeamInvite;
    }

    public boolean getOpenReg() {
        check();
        Boolean openReg = bcSysSettings.getOpenReg();
        if (null == openReg) {
            return false;
        }
        return openReg;
    }

    public boolean getRegTeamTenant() {
        check();
        Boolean regTeamTenant = bcSysSettings.getRegTeamTenant();
        if (null == regTeamTenant) {
            return false;
        }
        return regTeamTenant;
    }

    public TenantDomainEnum getDomainEnum() {
        check();
        return bcSysSettings.getTenantDomain();
    }

    private void check() {
        if (null == bcSysSettings) {
            bcSysSettings = sysSettingsService.sysSettings();
        }
    }

}

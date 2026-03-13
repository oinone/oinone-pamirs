package pro.shushi.pamirs.sys.setting.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.sys.setting.SysSettingModule;
import pro.shushi.pamirs.sys.setting.enmu.LoginTypeEnum;
import pro.shushi.pamirs.sys.setting.enmu.TenantDomainEnum;
import pro.shushi.pamirs.sys.setting.model.SysSettings;

import java.util.Collections;
import java.util.List;

/**
 * SysSettingInit
 *
 * @author yakir on 2022/11/27 20:10.
 */
@Slf4j
@Component
public class SysSettingInit implements InstallDataInit, UpgradeDataInit {

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        initBcSysSetting();
        return false;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initBcSysSetting();
        return false;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(SysSettingModule.MODULE_MODULE);
    }

    private void initBcSysSetting() {

        IWrapper<SysSettings> qw = Pops.<SysSettings>lambdaQuery()
                .from(SysSettings.MODEL_MODEL)
                .eq(SysSettings::getId, 1L);
        long count = Models.origin().count(qw);
        if (count > 0) {
            return;
        }

        SysSettings bcSysSettings = new SysSettings();
        bcSysSettings.setId(1L);
        bcSysSettings.setLoginType(LoginTypeEnum.NAME_PHONE);
        bcSysSettings.setOpenReg(true);
        bcSysSettings.setOpenDomainSetting(false);
        bcSysSettings.setOpenTenantSetting(false);
        bcSysSettings.setRegInvite(false);
        bcSysSettings.setRegTeamInvite(true);
        bcSysSettings.setRegTeamTenant(true);
        bcSysSettings.setTenantDomain(TenantDomainEnum.L3_DOMAIN);
        bcSysSettings.createOrUpdate();
        log.info("System configuration: [{}]", bcSysSettings);
    }
}
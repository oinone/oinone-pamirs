package pro.shushi.pamirs.sys.setting.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.sys.setting.api.SysSettingsService;
import pro.shushi.pamirs.sys.setting.model.SysSettings;

/**
 * SysSettingAction
 *
 * @author yakir on 2022/11/27 20:12.
 */
@Component
@Model.model(SysSettings.MODEL_MODEL)
public class SysSettingAction {

    @Autowired
    private SysSettingsService bcSysSettingsService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public SysSettings construct(SysSettings data) {
        return bcSysSettingsService.sysSettings();
    }
}

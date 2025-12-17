package pro.shushi.pamirs.trigger.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.SystemRole;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.trigger.TriggerModule;
import pro.shushi.pamirs.trigger.constant.TriggerUserConfiguration;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.enmu.UserType;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 触发器用户权限初始化
 *
 * @author Adamancy Zhang on 2021-01-30 21:20
 */
@Component
public class TriggerAuthUserInit implements InstallDataInit, UpgradeDataInit {

    @Autowired
    private UserService userService;

    @Override
    public List<String> modules() {
        return Collections.singletonList(TriggerModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        initTriggerUser();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initTriggerUser();
        return true;
    }

    private void initTriggerUser() {
        if (new PamirsUser().setCode(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_CODE).count() == 0) {
            PamirsUser triggerUser = (PamirsUser) new PamirsUser()
                    .setNickname(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_NAME)
                    .setUserType(UserType.SYSTEM.name())
                    .setSignUpType(UserSignUpType.BACKSTAGE)
                    .setName(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_NAME)
                    .setLogin(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_CODE)
                    .setSource(UserSourceEnum.BUILD_IN)
                    .setActive(Boolean.FALSE)
                    .setRoles(Arrays.asList(SystemRole.admin()))
                    .setCode(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_CODE)
                    .setId(TriggerUserConfiguration.TRIGGER_SYSTEM_USER_ID);
            userService.createOrUpdate(triggerUser);
        }
    }
}

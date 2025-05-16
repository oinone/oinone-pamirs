package pro.shushi.pamirs.eip.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;
import pro.shushi.pamirs.eip.api.model.AbstractEipApi;
import pro.shushi.pamirs.eip.api.model.EipLib;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.enmu.UserType;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Adamancy Zhang on 2021-01-30 21:20
 */
@Component
public class EipAuthUserInit implements LifecycleCompletedInit {

    @Autowired
    UserService userService;

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        initEipUser();
        initDefaultEipLib();
    }

    private void initEipUser() {
        List<AuthRole> roles = new ArrayList<>();
        roles.add(FetchUtil.onlyCreate(new AuthRole().setName(AuthConstants.SUPER_ROLE)).getData());
        Result<PamirsUser> userResult = FetchUtil.onlyCreate((PamirsUser) new PamirsUser()
                .setNickname(EipConfigurationConstant.EIP_SYSTEM_USER_NAME)
                .setSignUpType(UserSignUpType.BACKSTAGE)
                .setUserType(UserType.SYSTEM.name())
                .setName(EipConfigurationConstant.EIP_SYSTEM_USER_NAME)
                .setRealname("EIP系统用户")
                .setLogin(EipConfigurationConstant.EIP_SYSTEM_USER_CODE)
                .setActive(Boolean.FALSE)
                .setSource(UserSourceEnum.BUILD_IN)
                .setRoles(roles)
                .setRegDate(new Date())
                .setCode(EipConfigurationConstant.EIP_SYSTEM_USER_CODE)
                .setId(EipConfigurationConstant.EIP_SYSTEM_USER_ID));
        if (userResult.isSuccess()) {
            userResult.getData().fieldSave(PamirsUser::getRoles);
        }
    }

    private void initDefaultEipLib() {
        FetchUtil.onlyCreate(new EipLib().setName("未分类").setCode(AbstractEipApi.DEFAULT_LIB_CODE));
    }
}

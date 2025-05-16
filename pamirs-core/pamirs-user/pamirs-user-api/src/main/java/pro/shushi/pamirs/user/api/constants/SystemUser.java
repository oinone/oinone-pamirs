package pro.shushi.pamirs.user.api.constants;

import pro.shushi.pamirs.resource.api.enmu.UserSignUpType;
import pro.shushi.pamirs.user.api.configure.UserConfiguration;
import pro.shushi.pamirs.user.api.configure.UserConfigure;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.enmu.UserType;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * 系统用户
 *
 * @author Adamancy Zhang at 09:49 on 2024-01-05
 */
public class SystemUser {

    private SystemUser() {
        //reject create object
    }

    public static PamirsUser anonymous() {
        PamirsUser anonymous = new PamirsUser();
        anonymous.setSignUpType(UserSignUpType.BACKSTAGE)
                .setUserType(UserType.SYSTEM.name())
                .setLogin(UserConstants.ANONYMOUS_USER_LOGIN)
                .setSource(UserSourceEnum.BUILD_IN)
                .setActive(Boolean.FALSE)
                .setName(UserConstants.ANONYMOUS_USER_LOGIN)
                .setNickname(UserConstants.ANONYMOUS_USER_LOGIN)
                .setRealname(UserConstants.ANONYMOUS_USER_NICKNAME)
                .setCode(UserConstants.ANONYMOUS_USER_CODE)
                .setId(UserConstants.ANONYMOUS_USER_ID);
        return anonymous;
    }

    public static PamirsUser admin() {
        UserConfiguration.UserConfig userConfig = UserConfigure.getAdminConfig();
        PamirsUser admin = new PamirsUser();
        admin.setSignUpType(UserSignUpType.BACKSTAGE)
                .setUserType(UserType.SYSTEM.name())
                .setLogin(userConfig.getLogin())
                .setInitialPassword(userConfig.getPassword())
                .setSource(UserSourceEnum.BUILD_IN)
                .setActive(Boolean.TRUE)
                .setName(userConfig.getName())
                .setNickname(userConfig.getName())
                .setRealname(userConfig.getName())
                .setCode(UserConstants.ADMIN_USER_CODE)
                .setId(UserConstants.ADMIN_USER_ID);
        return admin;
    }
}

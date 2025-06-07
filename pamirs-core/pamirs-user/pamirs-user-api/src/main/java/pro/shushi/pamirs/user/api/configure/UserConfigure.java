package pro.shushi.pamirs.user.api.configure;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.constants.UserConstants;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * 用户配置
 *
 * @author Adamancy Zhang at 18:10 on 2024-06-15
 */
public class UserConfigure {

    private static final UserConfiguration.UserConfig DEFAULT_ADMIN_CONFIG = new UserConfiguration.UserConfig(
            UserConstants.ADMIN_USER_LOGIN,
            UserConstants.ADMIN_USER_PASSWORD,
            UserConstants.ADMIN_USER_NICKNAME
    );

    private static final int SESSION_EXPIRE_MIN = 60;

    public static UserConfiguration.UserConfig getAdminConfig() {
        return Optional.ofNullable(getUserConfiguration())
                .map(UserConfiguration::getAdmin)
                .map(adminConfig -> {
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getLogin, UserConfiguration.UserConfig::setLogin, DEFAULT_ADMIN_CONFIG.getLogin());
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getPassword, UserConfiguration.UserConfig::setPassword, DEFAULT_ADMIN_CONFIG.getPassword());
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getName, UserConfiguration.UserConfig::setName, DEFAULT_ADMIN_CONFIG.getName());
                    return adminConfig;
                })
                .orElse(DEFAULT_ADMIN_CONFIG);
    }

    public static UserConfiguration.SessionConfig getSessionConfig() {
        return Optional.ofNullable(getUserConfiguration())
                .map(UserConfiguration::getSession)
                .orElse(null);
    }

    public static int getDefaultSessionExpire() {
        return Optional.ofNullable(UserConfigure.getSessionConfig())
                .map(UserConfiguration.SessionConfig::getExpire)
                .filter(v -> v >= SESSION_EXPIRE_MIN)
                .orElse(UserConstant.USER_EXPIRE_TIME);
    }

    public static int getDefaultSessionRenewedExpire() {
        return Optional.ofNullable(UserConfigure.getSessionConfig())
                .map(UserConfiguration.SessionConfig::getRenewedExpire)
                .orElse(UserConstant.USER_EXPIRE_TIME);
    }

    public static Set<String> getRenewedFilterUrls() {
        return Optional.ofNullable(UserConfigure.getSessionConfig())
                .map(UserConfiguration.SessionConfig::getRenewedFilterUrls)
                .filter(CollectionUtils::isNotEmpty)
                .map(Collections::unmodifiableSet)
                .orElse(Collections.emptySet());
    }

    private static UserConfiguration getUserConfiguration() {
        return BeanDefinitionUtils.getBean(UserConfiguration.class);
    }
}

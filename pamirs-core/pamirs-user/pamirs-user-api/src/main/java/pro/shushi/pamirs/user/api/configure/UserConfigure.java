package pro.shushi.pamirs.user.api.configure;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
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

    private static final int SESSION_EXPIRE_MIN = 60;

    public static UserConfiguration.UserConfig getAdminConfig() {
        UserConfiguration.UserConfig defaultAdminConfig = new UserConfiguration.UserConfig(
                UserConstants.ADMIN_USER_LOGIN,
                UserConstants.ADMIN_USER_PASSWORD,
                I18nUtils.getMessage(UserConstants.ADMIN_USER_NICKNAME)
        );
        return Optional.ofNullable(getUserConfiguration())
                .map(UserConfiguration::getAdmin)
                .map(adminConfig -> {
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getLogin, UserConfiguration.UserConfig::setLogin, defaultAdminConfig.getLogin());
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getPassword, UserConfiguration.UserConfig::setPassword, defaultAdminConfig.getPassword());
                    VerificationHelper.setDefaultValue(adminConfig, UserConfiguration.UserConfig::getName, UserConfiguration.UserConfig::setName, defaultAdminConfig.getName());
                    return adminConfig;
                })
                .orElse(defaultAdminConfig);
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

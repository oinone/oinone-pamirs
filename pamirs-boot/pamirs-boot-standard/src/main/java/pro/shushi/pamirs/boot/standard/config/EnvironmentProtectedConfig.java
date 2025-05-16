package pro.shushi.pamirs.boot.standard.config;

import pro.shushi.pamirs.boot.common.util.ApplicationArgUtils;

/**
 * 环境保护配置
 *
 * @author Adamancy Zhang at 10:56 on 2024-10-12
 */
public class EnvironmentProtectedConfig {

    private static final String P_ENV_PROTECTED = ApplicationArgUtils.PREFIX_P + "envProtected";

    private static final String P_SAVE_ENVIRONMENT = ApplicationArgUtils.PREFIX_P + "saveEnvironments";

    private static final String P_STRICT_PROTECTED = ApplicationArgUtils.PREFIX_P + "strictProtected";

    public static boolean isEnabled() {
        return !Boolean.FALSE.toString().equals(ApplicationArgUtils.getArgs().getArgs().get(P_ENV_PROTECTED));
    }

    public static boolean isSaveEnvironments() {
        return !Boolean.FALSE.toString().equals(ApplicationArgUtils.getArgs().getArgs().get(P_SAVE_ENVIRONMENT));
    }

    public static boolean isStrict() {
        return Boolean.TRUE.toString().equals(ApplicationArgUtils.getArgs().getArgs().get(P_STRICT_PROTECTED));
    }
}

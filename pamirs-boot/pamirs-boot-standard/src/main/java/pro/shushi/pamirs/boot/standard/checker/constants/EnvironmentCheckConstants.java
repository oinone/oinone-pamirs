package pro.shushi.pamirs.boot.standard.checker.constants;

import pro.shushi.pamirs.locale.utils.I18nUtils;

/**
 * 环境检查常量
 *
 * @author Adamancy Zhang at 12:39 on 2024-10-17
 */
public class EnvironmentCheckConstants {

    public static final String IMMUTABLE_TIP = "EnvironmentCheckConstants.IMMUTABLE_TIP";

    public static final String PRODUCT_ENVIRONMENT_CLOSED_TIP = "EnvironmentCheckConstants.PRODUCT_ENVIRONMENT_CLOSED_TIP";

    public static final String PRODUCT_ENVIRONMENT_ENABLED_TIP = "EnvironmentCheckConstants.PRODUCT_ENVIRONMENT_ENABLED_TIP";

    public static String getImmutableTip() {
        return I18nUtils.getMessage(IMMUTABLE_TIP);
    }

    public static String getProductEnvironmentClosedTip() {
        return I18nUtils.getMessage(PRODUCT_ENVIRONMENT_CLOSED_TIP);
    }

    public static String getProductEnvironmentEnabledTip() {
        return I18nUtils.getMessage(PRODUCT_ENVIRONMENT_ENABLED_TIP);
    }

}

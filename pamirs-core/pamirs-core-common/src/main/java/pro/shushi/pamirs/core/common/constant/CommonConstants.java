package pro.shushi.pamirs.core.common.constant;

import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;

/**
 * 常量
 *
 * @author Adamancy Zhang at 16:08 on 2024-01-06
 */
public class CommonConstants {

    public static final String MANAGEMENT_CENTER_MODULE = "management_center";

    public static final String MANAGEMENT_CENTER_MODULE_NAME = "managementCenter";

    public static final String HOMEPAGE_DISPLAY_NAME = "首页";

    public static final String LOW_CODE_HOMEPAGE_SUFFIX = "_lowCode_homePage";

    public static final String TRANSLATE_PREFIX = "$t(";
    public static final String TRANSLATE_SUFFIX = ")";

    public static final String CLASSPATH_PROTOCOL = "classpath:";

    public static String getDefaultAppLogoUrl() {
        return FileClientFactory.getClient().getStaticUrl() + "/pamirs/image/logo/default.png";
    }

    public static String getDefaultBrandLogoUrl() {
        return FileClientFactory.getClient().getStaticUrl() + "/pamirs/image/logo/default_brand_logo.png";
    }
}

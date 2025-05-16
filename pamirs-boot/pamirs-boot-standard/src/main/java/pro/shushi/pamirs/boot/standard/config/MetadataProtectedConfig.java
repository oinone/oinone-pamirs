package pro.shushi.pamirs.boot.standard.config;

import pro.shushi.pamirs.boot.common.util.ApplicationArgUtils;

/**
 * 元数据保护配置
 *
 * @author Adamancy Zhang at 18:14 on 2024-08-07
 */
public class MetadataProtectedConfig {

    /**
     * 元数据保护标记
     */
    private static final String P_META_PROTECTED = ApplicationArgUtils.PREFIX_P + "metaProtected";

    /**
     * 元数据强制保护（用于修改元数据保护标记）
     */
    private static final String P_META_FORCE_PROTECTED = ApplicationArgUtils.PREFIX_P + "metaForceProtected";

    public static String getMetaProtected() {
        return ApplicationArgUtils.getArgs().getArgs().get(P_META_PROTECTED);
    }

    public static boolean isForceProtected() {
        return Boolean.TRUE.toString().equals(ApplicationArgUtils.getArgs().getArgs().get(P_META_FORCE_PROTECTED));
    }
}

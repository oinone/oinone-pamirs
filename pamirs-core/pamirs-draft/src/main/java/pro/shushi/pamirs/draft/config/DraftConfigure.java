package pro.shushi.pamirs.draft.config;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.draft.constant.DraftConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Optional;

/**
 * 草稿配置
 *
 * @author Adamancy Zhang at 15:30 on 2025-10-20
 */
public class DraftConfigure {

    public static String getStorage() {
        return Optional.ofNullable(getDraftConfiguration())
                .map(DraftConfiguration::getStorage)
                .filter(StringUtils::isNotBlank)
                .orElse(DraftConstants.DB_STORAGE);
    }

    public static DraftConfiguration.StrategyConfig getStrategyConfig() {
        return Optional.ofNullable(getDraftConfiguration())
                .map(DraftConfiguration::getStrategy)
                .orElse(null);
    }

    public static int getDefaultExpire() {
        return Optional.ofNullable(DraftConfigure.getStrategyConfig())
                .map(DraftConfiguration.StrategyConfig::getExpire)
                .orElse(-1);
    }

    private static DraftConfiguration getDraftConfiguration() {
        return BeanDefinitionUtils.getBean(DraftConfiguration.class);
    }
}

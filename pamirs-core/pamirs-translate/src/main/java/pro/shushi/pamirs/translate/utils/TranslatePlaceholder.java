package pro.shushi.pamirs.translate.utils;

import org.apache.commons.lang3.StringUtils;

import static pro.shushi.pamirs.translate.constant.TranslateConstants.TRANSLATE_PREFIX;
import static pro.shushi.pamirs.translate.constant.TranslateConstants.TRANSLATE_SUFFIX;

/**
 * TranslatePlaceholder
 *
 * @author yakir on 2023/10/13 11:35.
 */
@Deprecated
public class TranslatePlaceholder {

    @Deprecated
    public static String placeholder(String origin) {
        if (StringUtils.isNotBlank(origin) && !(origin.startsWith(TRANSLATE_PREFIX))) {
            return TRANSLATE_PREFIX + origin + TRANSLATE_SUFFIX;
        }
        return origin;
    }
}

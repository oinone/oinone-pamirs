package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;

import static pro.shushi.pamirs.core.common.constant.CommonConstants.TRANSLATE_PREFIX;
import static pro.shushi.pamirs.core.common.constant.CommonConstants.TRANSLATE_SUFFIX;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2023/9/26 10:34
 */
public class TranslateUtils {


    public static String translateByEnum(Object value, String displayName, String dict) {
        TranslateService translateService = TranslateServiceHolder.get();
        DataDictionary dictionary = PamirsSession.getContext().getDictionary(dict);
        dictionary = translateService.translateDictionary(dictionary);
        if (dictionary != null && CollectionUtils.isNotEmpty(dictionary.getOptions())) {
            return dictionary.getOptions().stream().filter(t -> t.getValue().equals(String.valueOf(value))).findFirst().map(DataDictionaryItem::getDisplayName).orElse(displayName);
        }
        return displayName;
    }

    public static String replaceByEnum(String text, String dict) {
        TranslateService translateService = TranslateServiceHolder.get();
        DataDictionary dictionary = PamirsSession.getContext().getDictionary(dict);
        translateService.translateDictionary(dictionary);

        if (StringUtils.isBlank(text)) return text;
        DataDictionary origin = PamirsSession.getContext().getDictionary(dict);
        if (CollectionUtils.isEmpty(origin.getOptions())) return text;
        for (DataDictionaryItem val : origin.getOptions()) {
            if (text.contains(val.getDisplayName())) {
                text = text.replace(val.getDisplayName(),
                        dictionary.getOptions().stream()
                                .filter(t -> t.getDisplayName().equals(val.getDisplayName()))
                                .findFirst().map(DataDictionaryItem::getDisplayName).orElse(val.getDisplayName()));
            }
        }
        return text;
    }

    public static String translateValues(String origin) {
        TranslateService translateService = TranslateServiceHolder.get();
        if (translateService.needTranslate()) {
            origin = placeholder(origin);
        }
        return origin;
    }

    public static String placeholder(String origin) {
        if (StringUtils.isNotBlank(origin) && !(origin.startsWith(TRANSLATE_PREFIX))) {
            return TRANSLATE_PREFIX + origin + TRANSLATE_SUFFIX;
        }
        return origin;
    }
}

package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * common i18n utils
 *
 * @author Adamancy Zhang at 18:05 on 2026-03-24
 */
public class CommonI18nUtils {

    private CommonI18nUtils() {
        // reject create object
    }

    public static String translateDataDictionaryItem(String module, String dictionary, IEnum<?> iEnum) {
        return I18nUtils.translateDataDictionaryItem(module, dictionary, iEnum.name(), "displayName", iEnum.displayName());
    }

    public static String translateErrorDefinition(ExpBaseEnum expEnum) {
        return I18nUtils.translateErrorDefinitionItem(expEnum.getClass().getName(), expEnum.name(), "msg", expEnum.msg());
    }
}

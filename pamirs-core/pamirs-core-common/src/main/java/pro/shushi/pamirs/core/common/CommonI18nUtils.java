package pro.shushi.pamirs.core.common;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * common i18n utils
 *
 * @author Adamancy Zhang at 18:05 on 2026-03-24
 */
public class CommonI18nUtils {

    private CommonI18nUtils() {
        // reject create object
    }

    public static String translateErrorDefinitionMsg(ExpBaseEnum expEnum) {
        return I18nUtils.translateErrorDefinitionItem(expEnum.getClass().getName(), expEnum.name(), "msg", expEnum.msg());
    }
}

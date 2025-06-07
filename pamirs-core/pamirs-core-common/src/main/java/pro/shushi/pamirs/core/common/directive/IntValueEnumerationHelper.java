package pro.shushi.pamirs.core.common.directive;

import java.util.Collection;

/**
 * <h>int value enumeration helper</h>
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 14:46
 */
public class IntValueEnumerationHelper {

    public static <T extends Enum<T> & IntValueEnumeration<T>> T intValueOf(Class<T> enumerationClass, int value) {
        return IntValueHelper.intValueOf(enumerationClass.getEnumConstants(), value);
    }

    public static <T extends Enum<T> & IntValueEnumeration<T>> Collection<T> intValuesOf(Class<T> enumerationClass, int value) {
        return IntValueHelper.intValuesOf(enumerationClass.getEnumConstants(), value);
    }
}

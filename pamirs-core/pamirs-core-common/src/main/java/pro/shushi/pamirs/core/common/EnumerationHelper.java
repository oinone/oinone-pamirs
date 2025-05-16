package pro.shushi.pamirs.core.common;

import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-02-07 14:23
 */
public class EnumerationHelper {

    private EnumerationHelper() {
        //reject create object
    }

    public static <T extends Enum<T>, V> T valueOf(Class<T> enumerationClass, V value, Function<T, V> getter) {
        if (enumerationClass == null) {
            return null;
        }
        boolean isNullValue = value == null;
        for (T enumItem : enumerationClass.getEnumConstants()) {
            V enumValue = getter.apply(enumItem);
            if (enumValue == null) {
                if (isNullValue) {
                    return enumItem;
                }
            } else {
                if (enumValue.equals(value)) {
                    return enumItem;
                }
            }
        }
        return null;
    }
}

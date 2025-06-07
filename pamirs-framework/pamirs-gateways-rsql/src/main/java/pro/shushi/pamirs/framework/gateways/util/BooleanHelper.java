package pro.shushi.pamirs.framework.gateways.util;

import pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Adamancy Zhang
 * @date 2021-01-04 20:20
 */
public class BooleanHelper {

    private static final String[] TRUE_STRINGS = {"true", "1"};

    private static final String[] FALSE_STRINGS = {"false", "0"};

    private BooleanHelper() {
        //reject create object
    }

    public static boolean isTrue(Object object) {
        return isTrue(object, () -> {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NULL_BOOL_FIELD_ERROR).errThrow();
        }, value -> {
            throw PamirsException.construct(RsqlExpEnumerate.BASE_NOT_AVAILABLE_BOOL_FIELD_ERROR).errThrow();
        });
    }

    public static boolean isFalse(Object object) {
        return !isTrue(object);
    }

    public static boolean isTrueWithoutException(Object object) {
        return isTrue(object, () -> false, value -> false);
    }

    public static boolean isFalseWithoutException(Object object) {
        return !isTrueWithoutException(object);
    }

    public static boolean isTrue(Object object, Supplier<Boolean> nullConsumer, Function<Object, Boolean> otherConsumer) {
        if (object instanceof Boolean) {
            return (boolean) object;
        } else if (object instanceof String) {
            String objectString = ((String) object).trim();
            for (String trueString : TRUE_STRINGS) {
                if (trueString.equalsIgnoreCase(objectString)) {
                    return true;
                }
            }
            for (String falseString : FALSE_STRINGS) {
                if (falseString.equalsIgnoreCase(objectString)) {
                    return false;
                }
            }
        } else {
            if (object == null) {
                return nullConsumer.get();
            }
            if (object instanceof Number) {
                return isTrue(String.valueOf(object), nullConsumer, otherConsumer);
            }
        }
        return otherConsumer.apply(object);
    }

    public static Boolean toBoolean(Object object) {
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            String objectString = ((String) object).trim();
            for (String trueString : TRUE_STRINGS) {
                if (trueString.equalsIgnoreCase(objectString)) {
                    return true;
                }
            }
            for (String falseString : FALSE_STRINGS) {
                if (falseString.equalsIgnoreCase(objectString)) {
                    return false;
                }
            }
        }
        return null;
    }
}

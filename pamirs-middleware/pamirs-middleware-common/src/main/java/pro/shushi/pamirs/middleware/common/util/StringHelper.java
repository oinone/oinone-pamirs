package pro.shushi.pamirs.middleware.common.util;

/**
 * @author Adamancy Zhang on 2021-04-28 18:18
 */
public class StringHelper {

    private static final String EMPTY = "";

    private StringHelper() {
        //reject create object
    }

    public static String valueOf(Object obj) {
        return valueOf(obj, EMPTY);
    }

    public static String valueOfNullable(Object obj) {
        return valueOf(obj, null);
    }

    public static String valueOf(Object obj, String defaultValue) {
        if (obj instanceof String) {
            return (String) obj;
        } else {
            if (obj == null) {
                return defaultValue;
            }
            return obj.toString();
        }
    }
}

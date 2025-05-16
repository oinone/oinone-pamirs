package pro.shushi.pamirs.core.common;

/**
 * {@link Integer}帮助类
 *
 * @author Adamancy Zhang at 18:11 on 2021-08-11
 */
public class IntegerHelper {

    private static final String MIN_NUMBER = "0";

    private static final String MAX_NUMBER = "9";

    private static final String MAX_VALUE_STRING = Integer.toString(Integer.MAX_VALUE);

    private IntegerHelper() {
        //reject create object
    }

    public static Integer getMaxValueBySize(int size) {
        if (size <= 0) {
            return 0;
        }
        StringBuilder builder = new StringBuilder();
        int dv = MAX_VALUE_STRING.length() - size;
        if (dv >= 0) {
            for (int i = 0; i < dv; i++) {
                builder.append(MIN_NUMBER);
            }
            for (int i = 0; i < size; i++) {
                builder.append(MAX_NUMBER);
            }
            String numberString = builder.toString();
            int compare = MAX_VALUE_STRING.compareTo(numberString);
            if (compare <= 0) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.valueOf(numberString);
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }
}

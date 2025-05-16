package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.entry.Holder;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Adamancy Zhang on 2021-02-24 21:37
 */
public class NumberHelper {

    private static final int PLUS_ASCLL = 43;

    private static final int MINUS_ASCLL = 45;

    private static final int POINT_ASCLL = 46;

    private static final int ZERO_ASCLL = 48;

    private static final int NINE_ASCLL = 57;

    private NumberHelper() {
        //reject create object
    }

    public static boolean isNumber(char c) {
        return c >= ZERO_ASCLL && c <= NINE_ASCLL;
    }

    /**
     * <h>判定字符串是否为数字</h>
     * <p>
     * 规则：
     * 1、只有一个字符时，只能为数字；
     * 2、首位只能是数字、加号、减号
     * 3、末位只能是数字
     * 4、中间字符只能是数字、点，且只能存在一个点
     * </p>
     *
     * @param s 字符串
     * @return 判断结果
     */
    public static boolean isNumber(String s) {
        Holder<Boolean> isDecimal = new Holder<>(false);
        return numberPredict(s, c -> {
            if (!isNumber(c)) {
                if (c == POINT_ASCLL) {
                    if (isDecimal.get()) {
                        return false;
                    } else {
                        isDecimal.set(true);
                    }
                } else {
                    return false;
                }
            }
            return true;
        });
    }

    /**
     * <h>判定字符串是否为整数</h>
     * <p>
     * 规则：
     * 1、只有一个字符时，只能为数字；
     * 2、首位只能是数字、加号、减号
     * 3、末位只能是数字
     * 4、中间字符只能是数字、点，且只能存在一个点
     * </p>
     *
     * @param s 字符串
     * @return 判断结果
     */
    public static boolean isInteger(String s) {
        return numberPredict(s, NumberHelper::isNumber);
    }

    /**
     * <h>数字判定</h>
     *
     * @param s 字符串
     * @return 判断结果
     */
    private static boolean numberPredict(String s, Predicate<Character> predicate) {
        if (StringUtils.isBlank(s)) {
            return false;
        }
        char[] cs = s.toCharArray();
        int l = cs.length, ll = l - 1;
        char first, last;
        boolean hasMiddle;
        if (l == 1) {
            return isNumber(cs[0]);
        } else if (l == 2) {
            first = cs[0];
            last = cs[1];
            hasMiddle = false;
        } else {
            first = cs[0];
            last = cs[ll];
            hasMiddle = true;
        }
        if (!isNumber(first) && first != PLUS_ASCLL && first != MINUS_ASCLL) {
            return false;
        }
        if (!isNumber(last)) {
            return false;
        }
        if (hasMiddle) {
            for (int i = 1; i < ll; i++) {
                char c = cs[i];
                if (!predicate.test(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static BigDecimal valueOf(Object value) {
        return valueOf(value, BigDecimal.ZERO);
    }

    public static BigDecimal valueOfNullable(Object value) {
        return valueOf(value, null);
    }

    public static BigDecimal valueOf(Object value, BigDecimal defaultValue) {
        return valueOf0(BigDecimal.class, value, defaultValue, BigDecimal::new);
    }

    public static long longValueOf(Object value) {
        return longValueOf(value, 0L);
    }

    public static Long longValueOfNullable(Object value) {
        return longValueOf(value, null);
    }

    public static Long longValueOf(Object value, Long defaultValue) {
        return valueOf0(Long.class, value, defaultValue, v -> new BigDecimal(v).longValue());
    }

    public static int intValueOf(Object value) {
        return intValueOf(value, 0);
    }

    public static Integer intValueOfNullable(Object value) {
        return intValueOf(value, null);
    }

    public static Integer intValueOf(Object value, Integer defaultValue) {
        return valueOf0(Integer.class, value, defaultValue, v -> new BigDecimal(v).intValue());
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueOf0(Class<T> cls, Object value, T defaultValue, Function<String, T> newInstanceFunction) {
        if (value == null) {
            return defaultValue;
        } else {
            Class<?> clz = value.getClass();
            if (cls.isAssignableFrom(clz)) {
                return (T) value;
            } else {
                String s = value.toString();
                if (isNumber(s)) {
                    return newInstanceFunction.apply(s);
                } else {
                    return defaultValue;
                }
            }
        }
    }
}

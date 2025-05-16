package pro.shushi.pamirs.meta.common.util;

import pro.shushi.pamirs.meta.common.enmu.BitEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.List;

/**
 * 位运算工具
 * <p>
 * 2020/7/30 12:55 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class BitUtil {

    public static Long enable(Long options, Long bit) {
        if (null == bit) {
            return options;
        }
        if (null == options) {
            options = 0L;
        }
        return options | bit;
    }

    @SuppressWarnings("unused")
    public static Long disable(Long options, Long bit) {
        if (null == options) {
            return null;
        }
        if (bit == null || bit <= 0) {
            return options;
        }
        return options & ~bit;
    }

    public static boolean has(Long options, Long bit) {
        if (null == options || null == bit) {
            return false;
        }
        return (options & bit) == bit;
    }

    public static boolean check(long bit) {
        return bit > 0 && (bit & bit - 1) == 0;
    }

    /**
     * 将Number转换为Long类型对象
     *
     * @param num 数字
     * @return 成功返回字符串对应的Long对象，失败返回null
     */
    public static Long longValue(Object num) {
        if (null == num) {
            return null;
        }
        if (num instanceof Number) {
            return ((Number) num).longValue();
        }
        if (num instanceof String) {
            return Long.parseLong((String) num);
        }
        if (num instanceof BitEnum) {
            return ((BitEnum) num).value();
        }
        if (num instanceof IEnum) {
            return longValue(((IEnum<?>) num).value());
        }
        return null;
    }

    public static long bitOr(Object[] params) {
        long sum = 0L;
        for (int i = 0; i < params.length; i++) {
            sum |= Long.parseLong(String.valueOf(((Object[]) params)[i]));
        }
        return sum;
    }

    public static long bitOr(List<?> params) {
        long sum = 0L;
        for (Object param : params) {
            sum |= Long.parseLong(String.valueOf(param));
        }
        return sum;
    }

}

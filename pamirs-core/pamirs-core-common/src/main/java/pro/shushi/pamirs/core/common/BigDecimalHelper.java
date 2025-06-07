package pro.shushi.pamirs.core.common;

import java.math.BigDecimal;
import java.util.function.Function;

public class BigDecimalHelper {

    public static String getDecimalString(BigDecimal currency) {
        return getDecimalString(null, currency, null, value -> value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public static String getDecimalString(BigDecimal currency, Function<BigDecimal, BigDecimal> function) {
        return getDecimalString(null, currency, null, function);
    }

    public static String getDecimalString(String prefix, BigDecimal currency) {
        return getDecimalString(prefix, currency, null, value -> value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public static String getDecimalString(String prefix, BigDecimal currency, Function<BigDecimal, BigDecimal> function) {
        return getDecimalString(prefix, currency, null, function);
    }

    public static String getDecimalString(BigDecimal currency, String suffix) {
        return getDecimalString(null, currency, suffix, value -> value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public static String getDecimalString(BigDecimal currency, String suffix, Function<BigDecimal, BigDecimal> function) {
        return getDecimalString(null, currency, suffix, function);
    }

    public static String getDecimalString(String prefix, BigDecimal currency, String suffix) {
        return getDecimalString(prefix, currency, suffix, value -> value.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public static String getDecimalString(String prefix, BigDecimal currency, String suffix, Function<BigDecimal, BigDecimal> function) {
        prefix = StringHelper.valueOf(prefix);
        suffix = StringHelper.valueOf(suffix);
        if (currency == null)
            currency = new BigDecimal("0");
        if (function != null)
            currency = function.apply(currency);
        return prefix + currency + suffix;
    }
}

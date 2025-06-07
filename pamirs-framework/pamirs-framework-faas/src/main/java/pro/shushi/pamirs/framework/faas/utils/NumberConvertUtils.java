package pro.shushi.pamirs.framework.faas.utils;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiConsumer;


/**
 * @author yeshenyue on 2024/9/3 17:45.
 */
@Slf4j
public class NumberConvertUtils {

    /**
     * 将参数转换成相同的数值类型
     */
    public static void convert(Object a, Object b, BiConsumer<Object, Object> consumer) {
        if (a.getClass().equals(b.getClass())) {
            consumer.accept(a, b);
            return;
        }
        NumberTypeWidget aType = NumberTypeWidget.fromClass(a.getClass());
        NumberTypeWidget bType = NumberTypeWidget.fromClass(b.getClass());
        if (aType.equals(bType)) {
            consumer.accept(a, b);
            return;
        } else if (aType.getWeight() > bType.getWeight()) {
            b = convert(b, aType.getClazz());
        } else {
            a = convert(a, bType.getClazz());
        }
        consumer.accept(a, b);
    }

    /**
     * 将参数转换成数值类型
     */
    public static Number convertNumber(Object n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Number) {
            return (Number) n;
        } else if (n instanceof String) {
            String str = (String) n;
            if (str.contains(".")) {
                return new BigDecimal(str);
            } else {
                return new Integer(str);
            }
        }
        log.error("尝试将{}转换成数值失败", n.toString());
        return null;
    }

    /**
     * 将参数根据指定的类进行转换
     */
    private static <R> Object convert(Object number, R clazz) {
        if (clazz.equals(BigDecimal.class)) {
            return new BigDecimal(number.toString());
        } else if (clazz.equals(BigInteger.class)) {
            return new BigInteger(number.toString());
        } else if (clazz.equals(Double.class)) {
            return Double.valueOf(number.toString());
        } else if (clazz.equals(Float.class)) {
            return Float.valueOf(number.toString());
        } else if (clazz.equals(Long.class)) {
            return Long.valueOf(number.toString());
        } else if (clazz.equals(Integer.class)) {
            return Integer.valueOf(number.toString());
        } else {
            log.error("数值转换失败，数值{}尝试转换成{}失败", number.toString(), clazz.getClass().getName());
            return null;
        }
    }


    private enum NumberTypeWidget {

        // 无法识别的类型
        UNKNOWN(-1, Object.class),
        STRING(0, String.class),
        INTEGER(1, Integer.class),
        LONG(2, Long.class),
        FLOAT(3, Float.class),
        DOUBLE(4, Double.class),
        BIGINTEGER(5, BigInteger.class),
        BIG_DECIMAL(6, BigDecimal.class),
        ;

        private final int weight;
        private final Class<?> clazz;

        NumberTypeWidget(int weight, Class<?> clazz) {
            this.weight = weight;
            this.clazz = clazz;
        }

        public int getWeight() {
            return weight;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public static NumberTypeWidget fromClass(Class<?> clazz) {
            if (clazz.equals(BigDecimal.class)) {
                return BIG_DECIMAL;
            } else if (clazz.equals(BigInteger.class)) {
                return BIGINTEGER;
            } else if (clazz.equals(Double.class)) {
                return DOUBLE;
            } else if (clazz.equals(Float.class)) {
                return FLOAT;
            } else if (clazz.equals(Long.class)) {
                return LONG;
            } else if (clazz.equals(Integer.class)) {
                return INTEGER;
            } else if (clazz.equals(String.class)) {
                return STRING;
            } else {
                return UNKNOWN;
            }
        }
    }
}

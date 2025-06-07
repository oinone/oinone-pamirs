package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import pro.shushi.pamirs.core.common.directive.Directive;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.StringJoiner;
import java.util.function.Function;

public class StringHelper {

    private static final int UPPER_A_ASCLL = 65;

    private static final int UPPER_Z_ASCLL = 90;

    private static final int LOWER_A_ASCLL = 97;

    private static final int LOWER_Z_ASCLL = 122;

    private static final char[] WHITE_SPACE_CHARS = new char[]{' ', '\t', '\n', 11, '\r', '\f'};

    /**
     * 是否是大写字母
     *
     * @param c 字符
     * @return 判断结果
     */
    public static boolean isUpperCase(char c) {
        return UPPER_A_ASCLL <= c && c <= UPPER_Z_ASCLL;
    }

    /**
     * 是否是小写字母
     *
     * @param c 字符
     * @return 判断结果
     */
    public static boolean isLowerCase(char c) {
        return LOWER_A_ASCLL <= c && c <= LOWER_Z_ASCLL;
    }

    /**
     * 是否是字母
     *
     * @param c 字符
     * @return 判断结果
     */
    public static boolean isLetter(char c) {
        return isUpperCase(c) || isLowerCase(c);
    }

    public static String valueOf(Object obj, Feature... features) {
        return valueOf(obj, CharacterConstants.SEPARATOR_EMPTY, features);
    }

    public static String valueOfNullable(Object obj, Feature... features) {
        return valueOf(obj, null, features);
    }

    public static String valueOf(Object obj, String defaultValue, Feature... features) {
        if (obj instanceof String) {
            return (String) obj;
        } else {
            if (obj == null) {
                return defaultValue;
            } else {
                int origin = DirectiveHelper.enable(0, features);
                final Object originObject = obj;
                Holder<Object> valueHolder = new Holder<>(obj);
                DirectiveHelper.execute(origin, features, feature -> {
                    Object temp = feature.converter.convert(originObject, valueHolder.get());
                    if (temp != null) {
                        valueHolder.set(temp);
                    }
                });
                return valueHolder.get().toString();
            }
        }
    }

    public static boolean equals(String a, String b) {
        if (StringUtils.isBlank(a) && StringUtils.isBlank(b)) {
            return true;
        }
        if (a != null) {
            return a.equals(b);
        }
        return false;
    }

    public static boolean isBlank(Object obj) {
        if (obj instanceof String) {
            return StringUtils.isBlank((String) obj);
        } else {
            return obj == null;
        }
    }

    public static boolean isNotBlank(Object obj) {
        return !isBlank(obj);
    }

    public static String trim(String s) {
        if (s == null) {
            return null;
        } else {
            return s.trim();
        }
    }

    @SafeVarargs
    public static <T> String join(CharSequence delimiter, Function<T, CharSequence> function, T... elements) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T element : elements) {
            joiner.add(function.apply(element));
        }
        return joiner.toString();
    }

    public static <T> String join(CharSequence delimiter, Function<T, String> function, Iterable<T> elements) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T element : elements) {
            joiner.add(function.apply(element));
        }
        return joiner.toString();
    }

    public static String join(CharSequence delimiter, CharSequence... elements) {
        return StringHelper.join(delimiter, element -> {
            if (element instanceof String) {
                return ((String) element).trim();
            } else {
                return element;
            }
        }, elements);
    }

    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        return StringHelper.join(delimiter, element -> {
            if (element instanceof String) {
                return ((String) element).trim();
            } else {
                return StringHelper.valueOf(element);
            }
        }, elements);
    }

    public static void appendCompactedString(StringBuilder destination, String source) {
        boolean previousIsWhiteSpace = false;
        int length = source.length();

        for (int i = 0; i < length; ++i) {
            char ch = source.charAt(i);
            if (isWhiteSpace(ch)) {
                if (!previousIsWhiteSpace) {
                    destination.append(' ');
                    previousIsWhiteSpace = true;
                }
            } else {
                destination.append(ch);
                previousIsWhiteSpace = false;
            }
        }
    }

    public static String concat(String split, String base, String... ss) {
        StringBuilder builder = new StringBuilder(base);
        for (String s : ss) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            builder.append(split).append(s);
        }
        return builder.toString();
    }

    public static boolean isWhiteSpace(char ch) {
        for (char c : WHITE_SPACE_CHARS) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    public enum Feature implements Directive {

        ENUMERATION_TO_VALUE(2 >> 1, (origin, target) -> {
            if (target instanceof IEnum) {
                IEnum<?> enumerationObject = (IEnum<?>) target;
                Object value = enumerationObject.value();
                if (value == null) {
                    return CharacterConstants.SEPARATOR_EMPTY;
                }
                return value;
            }
            return null;
        }),
        ENUMERATION_TO_NAME(2 >> 1, (origin, target) -> {
            if (target instanceof IEnum) {
                IEnum<?> enumerationObject = (IEnum<?>) target;
                Object value = enumerationObject.name();
                if (value == null) {
                    return CharacterConstants.SEPARATOR_EMPTY;
                }
                return value;
            }
            return null;
        }),
        ENUMERATION_TO_DISPLAY_NAME(2 >> 1, (origin, target) -> {
            if (target instanceof IEnum) {
                IEnum<?> enumerationObject = (IEnum<?>) target;
                Object value = enumerationObject.displayName();
                if (value == null) {
                    return CharacterConstants.SEPARATOR_EMPTY;
                }
                return value;
            }
            return null;
        }),

        DATETIME_FORMAT(2 << 1, (origin, target) -> {
            if (target instanceof Date) {
                return DateFormatUtils.format((Date) target, DateFormatEnum.DATETIME.value());
            }
            return null;
        }),
        DATE_FORMAT(2 << 1, (origin, target) -> {
            if (target instanceof Date) {
                return DateFormatUtils.format((Date) target, DateFormatEnum.DATE.value());
            }
            return null;
        }),
        TIME_FORMAT(2 << 1, (origin, target) -> {
            if (target instanceof Date) {
                return DateFormatUtils.format((Date) target, DateFormatEnum.TIME.value());
            }
            return null;
        }),

        DECIMAL_FORMAT_0(2 << 2, (origin, target) -> {
            BigDecimal decimal = NumberHelper.valueOfNullable(target);
            if (decimal != null) {
                return decimal.setScale(0, RoundingMode.HALF_UP).toString();
            }
            return null;
        }),
        DECIMAL_FORMAT_2(2 << 2, (origin, target) -> {
            BigDecimal decimal = NumberHelper.valueOfNullable(target);
            if (decimal != null) {
                return decimal.setScale(2, RoundingMode.HALF_UP).toString();
            }
            return null;
        }),
        DECIMAL_FORMAT_6(2 << 2, (origin, target) -> {
            BigDecimal decimal = NumberHelper.valueOfNullable(target);
            if (decimal != null) {
                return decimal.setScale(6, RoundingMode.HALF_UP).toString();
            }
            return null;
        });

        private final int intValue;
        private final Converter converter;

        Feature(int intValue, Converter converter) {
            this.intValue = intValue;
            this.converter = converter;
        }

        @Override
        public int intValue() {
            return intValue;
        }
    }

    @FunctionalInterface
    public interface Converter {

        /**
         * 转换
         *
         * @param origin 原对象
         * @param target 当前对象
         * @return 最终结果
         */
        Object convert(Object origin, Object target);
    }
}

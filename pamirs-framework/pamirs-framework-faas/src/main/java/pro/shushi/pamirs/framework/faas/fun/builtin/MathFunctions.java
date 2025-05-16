package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import pro.shushi.pamirs.framework.common.utils.MoneyUtils;
import pro.shushi.pamirs.framework.faas.utils.NumberConvertUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Holder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.MATH;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 数学函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
@Fun(NamespaceConstants.expression)
public class MathFunctions {

    @Function.Advanced(
            displayName = "绝对值", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("ABS")
    @Function(name = "ABS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ABS(number)\n函数说明: 获取number的绝对值"
    )
    public static Integer ABS(Integer number) {
        if (null == number) {
            return null;
        }
        return Math.abs(number);
    }

    public static Long ABS(Long number) {
        if (null == number) {
            return null;
        }
        return Math.abs(number);
    }

    public static Float ABS(Float number) {
        if (null == number) {
            return null;
        }
        return Math.abs(number);
    }

    public static Double ABS(Double number) {
        if (null == number) {
            return null;
        }
        return Math.abs(number);
    }

    @Function.Advanced(
            displayName = "向下取整", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("FLOOR")
    @Function(name = "FLOOR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: FLOOR(number)\n函数说明: 对number向下取整"
    )
    public static Double floor(Double number) {
        if (null == number) {
            return null;
        }
        return Math.floor(number);
    }

    @Function.Advanced(
            displayName = "向上取整", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("CEIL")
    @Function(name = "CEIL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CEIL(number)\n函数说明: 对number向上取整"
    )
    public static Double ceil(Double number) {
        if (null == number) {
            return null;
        }
        return Math.ceil(number);
    }

    @Function.Advanced(
            displayName = "四舍五入", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("ROUND")
    @Function(name = "ROUND", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ROUND(number)\n函数说明: 对number四舍五入"
    )
    public static Long round(Double number) {
        if (null == number) {
            return null;
        }
        return Math.round(number);
    }

    public static Integer round(Float number) {
        if (null == number) {
            return null;
        }
        return Math.round(number);
    }

    public static BigDecimal round(BigDecimal number) {
        if (null == number) {
            return null;
        }
        return number.setScale(0, RoundingMode.HALF_UP);
    }

    @Function.Advanced(
            displayName = "取余", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("MOD")
    @Function(name = "MOD", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MOD(A,B)\n函数说明: A对B取余"
    )
    public static Long mod(Long a, Long b) {
        if (null == a || null == b) {
            return null;
        }
        return Math.floorMod(a, b);
    }

    public static Long mod(Long a, Integer b) {
        if (null == a || null == b) {
            return null;
        }
        return Math.floorMod(a, b);
    }

    public static Long mod(Integer a, Long b) {
        if (null == a || null == b) {
            return null;
        }
        return Math.floorMod(a, b);
    }

    public static Integer mod(Integer a, Integer b) {
        if (null == a || null == b) {
            return null;
        }
        return Math.floorMod(a, b);
    }

    @Function.Advanced(
            displayName = "平方根", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("SQRT")
    @Function(name = "SQRT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SQRT(number)\n函数说明: 对number取平方根"
    )
    public static Double sqrt(Double number) {
        if (null == number) {
            return null;
        }
        return Math.sqrt(number);
    }

    @Function.Advanced(
            displayName = "正弦", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("SIN")
    @Function(name = "SIN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SIN(number)\n函数说明: 对number取正弦"
    )
    public static Double sin(Double number) {
        if (null == number) {
            return null;
        }
        return Math.sin(number);
    }

    @Function.Advanced(
            displayName = "余弦", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("COS")
    @Function(name = "COS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: COS(number)\n函数说明: 对number取余弦"
    )
    public static Double cos(Double number) {
        if (null == number) {
            return null;
        }
        return Math.cos(number);
    }

    @Function.Advanced(
            displayName = "圆周率", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("PI")
    @Function(name = "PI", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: PI()\n函数说明: 圆周率"
    )
    public static Double pi() {
        return Math.PI;
    }

    @Function.Advanced(
            displayName = "相加", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("ADD")
    @Function(name = "ADD", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ADD(A,B)\n函数说明: A与B相加"
    )
    public static Number add(Object n1, Object n2) {
        if (null == n1 && null == n2) {
            return null;
        } else if (null == n1) {
            return NumberConvertUtils.convertNumber(n2);
        } else if (null == n2) {
            return NumberConvertUtils.convertNumber(n1);
        }

        final Holder<Number> result = new Holder<>();
        NumberConvertUtils.convert(n1, n2, (a, b) -> {
            if (a == null || b == null) {
                Object resObj = null != b ? (Number) b : (null != a ? (Number) a : null);
                result.set(NumberConvertUtils.convertNumber(resObj));
            } else if (a instanceof Integer) {
                result.set((Integer) a + (Integer) b);
            } else if (a instanceof Long) {
                result.set((Long) a + (Long) b);
            } else if (a instanceof Float) {
                result.set((Float) a + (Float) b);
            } else if (a instanceof Double) {
                result.set((Double) a + (Double) b);
            } else if (a instanceof BigInteger) {
                result.set(((BigInteger) a).add((BigInteger) b));
            } else if (a instanceof BigDecimal) {
                result.set(((BigDecimal) a).add((BigDecimal) b));
            }
        });
        if (ObjectUtils.isEmpty(result.get())) {
            log.error("ADD函数发生意外情况：n1:{}, n2:{}，n1类型：{}, n2类型：{}", n1, n2, n1.getClass(), n2.getClass());
        }
        return result.get();
    }

    @Function.Advanced(
            displayName = "相减", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("SUBTRACT")
    @Function(name = "SUBTRACT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SUBTRACT(A,B)\n函数说明: A与B相减"
    )
    public static Number subtract(Object n1, Object n2) {
        if (null == n1 && null == n2) {
            return null;
        } else if (null == n1) {
            n1 = 0;
        } else if (null == n2) {
            n2 = 0;
        }

        final Holder<Number> result = new Holder<>();
        NumberConvertUtils.convert(n1, n2, (a, b) -> {
            if (a instanceof Integer) {
                result.set((Integer) a - (Integer) b);
            } else if (a instanceof Long) {
                result.set((Long) a - (Long) b);
            } else if (a instanceof Float) {
                result.set((Float) a - (Float) b);
            } else if (a instanceof Double) {
                result.set((Double) a - (Double) b);
            } else if (a instanceof BigInteger) {
                result.set(((BigInteger) a).subtract((BigInteger) b));
            } else if (a instanceof BigDecimal) {
                result.set(((BigDecimal) a).subtract((BigDecimal) b));
            }
        });
        if (ObjectUtils.isEmpty(result.get())) {
            log.error("SUBTRACT函数发生意外情况：n1:{}, n2:{}，n1类型：{}, n2类型：{}", n1, n2, n1.getClass(), n2.getClass());
        }
        return result.get();
    }

    @Function.Advanced(
            displayName = "乘积", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("MULTIPLY")
    @Function(name = "MULTIPLY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MULTIPLY(A,B)\n函数说明: A与B相乘"
    )
    public static Number multiply(Object n1, Object n2) {
        if (null == n1 || null == n2) {
            return null;
        }

        final Holder<Number> result = new Holder<>();
        NumberConvertUtils.convert(n1, n2, (a, b) -> {
            if (a instanceof Integer) {
                result.set((Integer) a * (Integer) b);
            } else if (a instanceof Long) {
                result.set((Long) a * (Long) b);
            } else if (a instanceof Float) {
                result.set((Float) a * (Float) b);
            } else if (a instanceof Double) {
                result.set((Double) a * (Double) b);
            } else if (a instanceof BigInteger) {
                result.set(((BigInteger) a).multiply((BigInteger) b));
            } else if (a instanceof BigDecimal) {
                result.set(((BigDecimal) a).multiply((BigDecimal) b));
            }
        });
        if (ObjectUtils.isEmpty(result.get())) {
            log.error("MULTIPLY函数发生意外情况：n1:{}, n2:{}，n1类型：{}, n2类型：{}", n1, n2, n1.getClass(), n2.getClass());
        }
        return result.get();
    }

    @Function.Advanced(
            displayName = "相除", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("DIVIDE")
    @Function(name = "DIVIDE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: DIVIDE(A,B)\n函数说明: A与B相除"
    )
    public static Number divide(Object n1, Object n2) {
        if (null == n1 || null == n2) {
            return null;
        }

        final Holder<Number> result = new Holder<>();
        NumberConvertUtils.convert(n1, n2, (a, b) -> {
            if (a instanceof Integer) {
                result.set((Integer) a / (Integer) b);
            } else if (a instanceof Long) {
                result.set((Long) a / (Long) b);
            } else if (a instanceof Float) {
                result.set((Float) a / (Float) b);
            } else if (a instanceof Double) {
                result.set((Double) a / (Double) b);
            } else if (a instanceof BigInteger) {
                result.set(((BigInteger) a).divide((BigInteger) b));
            } else if (a instanceof BigDecimal) {
                result.set(((BigDecimal) a).divide((BigDecimal) b));
            }
        });
        if (ObjectUtils.isEmpty(result.get())) {
            log.error("DIVIDE函数发生意外情况：n1:{}, n2:{}，n1类型：{}, n2类型：{}", n1, n2, n1.getClass(), n2.getClass());
        }
        return result.get();
    }

    @Function.Advanced(
            displayName = "取最大值", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("MAX")
    @Function(name = "MAX", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAX(collection)\n函数说明: 返回集合中的最大值，参数collection为集合或数组"
    )
    public static Number max(Number... collection) {
        if (ArrayUtils.isEmpty(collection)) {
            return null;
        }
        Number temp = null;
        for (Number obj : collection) {
            if (null == temp) {
                temp = obj;
            } else {
                if (obj instanceof Integer) {
                    temp = Math.max((Integer) obj, (Integer) temp);
                } else if (obj instanceof Long) {
                    temp = Math.max((Long) obj, (Long) temp);
                } else if (obj instanceof Float) {
                    temp = Math.max((Float) obj, (Float) temp);
                } else if (obj instanceof Double) {
                    temp = Math.max((Double) obj, (Double) temp);
                } else if (obj instanceof BigInteger) {
                    temp = ((BigInteger) obj).compareTo((BigInteger) temp) >= 0 ? obj : temp;
                } else if (obj instanceof BigDecimal) {
                    temp = ((BigDecimal) obj).compareTo((BigDecimal) temp) >= 0 ? obj : temp;
                }
            }
        }
        return temp;
    }

    public static Number max(List<Number> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        Number temp = null;
        for (Number obj : collection) {
            if (null == temp) {
                temp = obj;
            } else {
                if (obj instanceof Integer) {
                    temp = Math.max((Integer) obj, (Integer) temp);
                } else if (obj instanceof Long) {
                    temp = Math.max((Long) obj, (Long) temp);
                } else if (obj instanceof Float) {
                    temp = Math.max((Float) obj, (Float) temp);
                } else if (obj instanceof Double) {
                    temp = Math.max((Double) obj, (Double) temp);
                } else if (obj instanceof BigInteger) {
                    temp = ((BigInteger) obj).compareTo((BigInteger) temp) >= 0 ? obj : temp;
                } else if (obj instanceof BigDecimal) {
                    temp = ((BigDecimal) obj).compareTo((BigDecimal) temp) >= 0 ? obj : temp;
                }
            }
        }
        return temp;
    }

    @Function.Advanced(
            displayName = "取最小值", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("MIN")
    @Function(name = "MIN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MIN(collection)\n函数说明: 返回集合中的最小值，参数collection为集合或数组"
    )
    public static Number min(List<Number> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        Number temp = null;
        for (Number obj : collection) {
            if (null == temp) {
                temp = obj;
            } else {
                if (obj instanceof Integer) {
                    temp = Math.min((Integer) obj, (Integer) temp);
                } else if (obj instanceof Long) {
                    temp = Math.min((Long) obj, (Long) temp);
                } else if (obj instanceof Float) {
                    temp = Math.min((Float) obj, (Float) temp);
                } else if (obj instanceof Double) {
                    temp = Math.min((Double) obj, (Double) temp);
                } else if (obj instanceof BigInteger) {
                    temp = ((BigInteger) obj).compareTo((BigInteger) temp) < 0 ? obj : temp;
                } else if (obj instanceof BigDecimal) {
                    temp = ((BigDecimal) obj).compareTo((BigDecimal) temp) < 0 ? obj : temp;
                }
            }
        }
        return temp;
    }

    public static Number min(Number... collection) {
        if (ArrayUtils.isEmpty(collection)) {
            return null;
        }
        Number temp = null;
        for (Number obj : collection) {
            if (null == temp) {
                temp = obj;
            } else {
                if (obj instanceof Integer) {
                    temp = Math.min((Integer) obj, (Integer) temp);
                } else if (obj instanceof Long) {
                    temp = Math.min((Long) obj, (Long) temp);
                } else if (obj instanceof Float) {
                    temp = Math.min((Float) obj, (Float) temp);
                } else if (obj instanceof Double) {
                    temp = Math.min((Double) obj, (Double) temp);
                } else if (obj instanceof BigInteger) {
                    temp = ((BigInteger) obj).compareTo((BigInteger) temp) < 0 ? obj : temp;
                } else if (obj instanceof BigDecimal) {
                    temp = ((BigDecimal) obj).compareTo((BigDecimal) temp) < 0 ? obj : temp;
                }
            }
        }
        return temp;
    }

    @Function.Advanced(
            displayName = "求和", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("SUM")
    @Function(name = "SUM", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: SUM(collection)\n函数说明: 返回对集合的求和，参数collection为集合或数组"
    )
    public static Number sum(List<Number> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        Number sum = null;
        for (Number obj : collection) {
            if (null == sum) {
                sum = obj;
            } else {
                if (obj instanceof Integer) {
                    sum = (Integer) sum + (Integer) obj;
                } else if (obj instanceof Long) {
                    sum = (Long) sum + (Long) obj;
                } else if (obj instanceof Float) {
                    sum = (Float) sum + (Float) obj;
                } else if (obj instanceof Double) {
                    sum = (Double) sum + (Double) obj;
                } else if (obj instanceof BigInteger) {
                    sum = ((BigInteger) obj).add(new BigInteger(sum + ""));
                } else if (obj instanceof BigDecimal) {
                    sum = ((BigDecimal) obj).add(new BigDecimal(sum + ""));
                }
            }
        }
        return sum;
    }

    public static Number sum(Number... collection) {
        if (ArrayUtils.isEmpty(collection)) {
            return null;
        }
        Number sum = null;
        for (Number obj : collection) {
            if (null == sum) {
                sum = obj;
            } else {
                if (obj instanceof Integer) {
                    sum = (Integer) sum + (Integer) obj;
                } else if (obj instanceof Long) {
                    sum = (Long) sum + (Long) obj;
                } else if (obj instanceof Float) {
                    sum = (Float) sum + (Float) obj;
                } else if (obj instanceof Double) {
                    sum = (Double) sum + (Double) obj;
                } else if (obj instanceof BigInteger) {
                    sum = ((BigInteger) obj).add(new BigInteger(sum + ""));
                } else if (obj instanceof BigDecimal) {
                    sum = ((BigDecimal) obj).add(new BigDecimal(sum + ""));
                }
            }
        }
        return sum;
    }

    @Function.Advanced(
            displayName = "取平均值", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("AVG")
    @Function(name = "AVG", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: AVG(collection)\n函数说明: 返回集合的平均值，参数collection为集合或数组"
    )
    public static Number avg(List<Number> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        Number obj = collection.get(0);
        if (obj instanceof Integer) {
            return ((Integer) sum(collection)) / collection.size();
        } else if (obj instanceof Long) {
            return ((Long) sum(collection)) / collection.size();
        } else if (obj instanceof Float) {
            return ((Float) sum(collection)) / collection.size();
        } else if (obj instanceof Double) {
            return ((Double) sum(collection)) / collection.size();
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) sum(collection)).divide(new BigInteger(collection.size() + ""));
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) sum(collection)).divide(new BigDecimal(collection.size()));
        }
        return null;
    }

    public static Number avg(Number... collection) {
        if (ArrayUtils.isEmpty(collection)) {
            return null;
        }
        Number obj = collection[0];
        if (obj instanceof Integer) {
            return ((Integer) sum(collection)) / collection.length;
        } else if (obj instanceof Long) {
            return ((Long) sum(collection)) / collection.length;
        } else if (obj instanceof Float) {
            return ((Float) sum(collection)) / collection.length;
        } else if (obj instanceof Double) {
            return ((Double) sum(collection)) / collection.length;
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) sum(collection)).divide(new BigInteger(collection.length + ""));
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) sum(collection)).divide(new BigDecimal(collection.length));
        }
        return null;
    }

    @Function.Advanced(
            displayName = "计数", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("COUNT")
    @Function(name = "COUNT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: COUNT(collection)\n函数说明: 返回集合的总数，参数collection为集合或数组"
    )
    public static Integer count(Object... collection) {
        if (null == collection) {
            return 0;
        }
        if (1 == collection.length && collection[0] instanceof List) {
            return ((List) collection[0]).size();
        }
        return collection.length;
    }

    @Function.Advanced(
            displayName = "大写金额", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("UPPER_MONEY")
    @Function(name = "UPPER_MONEY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: UPPER_MONEY(number)\n函数说明: 返回金额的大写，参数number为数值或数值类型的字符串"
    )
    public static String upperMoney(Number number) {
        String numberString = number + "";
        if (numberString.matches("^[+-]?\\\\d+(\\\\.\\\\d+)?$")) {
            return "金额数据格式错误，金额" + numberString;
        }
        return MoneyUtils.toChinese(new BigDecimal(numberString).toString());
    }

    @Function.Advanced(
            displayName = "相减差非负", language = JAVA,
            builtin = true, category = MATH
    )
    @Function.fun("NNZ_SUBTRACT")
    @Function(name = "NNZ_SUBTRACT", scene = {EXPRESSION}, openLevel = {LOCAL},
            summary = "函数示例: NNZ_SUBTRACT(A,B)\n函数说明: A与B相减，差大于等于0就直接用这个差，差小于0就把差的值设置为0"
    )
    public static Number nnzSubtract(Number a, Number b) {
        if (null != a && null != b) {
            if (a instanceof Integer) {
                return Math.max((Integer) a - (Integer) b, 0);
            } else if (a instanceof Long) {
                return Math.max((Long) a - (Long) b, 0);
            } else if (a instanceof Float) {
                return Math.max((Float) a - (Float) b, 0);
            } else if (a instanceof Double) {
                return Math.max((Double) a - (Double) b, 0);
            } else if (a instanceof BigInteger) {
                return ((BigInteger) a).subtract((BigInteger) b).compareTo(BigInteger.ZERO) > 0 ? ((BigInteger) a).subtract((BigInteger) b) : 0;
            } else {
                return a instanceof BigDecimal ? ((BigDecimal) a).subtract((BigDecimal) b).compareTo(BigDecimal.ZERO) > 0 ? ((BigDecimal) a).subtract((BigDecimal) b) : BigDecimal.ZERO : null;
            }
        } else {
            return null;
        }
    }

}

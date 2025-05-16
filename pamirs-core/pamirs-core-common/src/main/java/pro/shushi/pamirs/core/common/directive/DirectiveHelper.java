package pro.shushi.pamirs.core.common.directive;

import java.util.function.Consumer;

/**
 * 指令帮助类
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 14:47
 */
public class DirectiveHelper {

    private DirectiveHelper() {
        //reject create object
    }

    public static int enable(int origin, int target) {
        return origin | target;
    }

    public static long enable(long origin, long target) {
        return origin | target;
    }

    public static <T extends Directive> int enable(int origin, T target) {
        return enable(origin, target.intValue());
    }

    public static int disable(int origin, int target) {
        return origin & ~target;
    }

    public static <T extends Directive> int disable(int origin, T target) {
        return disable(origin, target.intValue());
    }

    public static <T extends Directive> int enable(T[] targets) {
        return enable(0, targets);
    }

    public static <T extends Directive> int enable(int origin, T[] targets) {
        for (T target : targets) {
            origin |= target.intValue();
        }
        return origin;
    }

    public static <T extends Directive> int disable(int origin, T[] targets) {
        for (T target : targets) {
            origin &= ~target.intValue();
        }
        return origin;
    }

    public static <T extends Directive> int enable(int origin, Iterable<T> targets) {
        for (T target : targets) {
            origin |= target.intValue();
        }
        return origin;
    }

    public static <T extends Directive> int disable(int origin, Iterable<T> targets) {
        for (T target : targets) {
            origin &= ~target.intValue();
        }
        return origin;
    }

    public static <T extends Directive> boolean isEnabled(int origin, T target) {
        return isEnabled(origin, target.intValue());
    }

    public static <T extends Directive> boolean isDisabled(int origin, T target) {
        return isDisabled(origin, target.intValue());
    }

    public static <T extends Directive> boolean isEnabled(int origin, int target) {
        return (origin & target) != 0;
    }

    public static <T extends Directive> boolean isEnabled(int origin, long target) {
        return (origin & target) != 0;
    }

    public static <T extends Directive> boolean isDisabled(int origin, int target) {
        return (origin & target) == 0;
    }

    public static <T extends Directive> boolean isDisabled(int origin, long target) {
        return (origin & target) == 0;
    }

    public static boolean isBitValue(int value) {
        return value >= 1 && (value & value - 1) == 0;
    }

    public static boolean isBitValue(long value) {
        return value >= 1 && (value & value - 1) == 0;
    }

    /**
     * <h>验证指令组有效性</h>
     * <p>
     * 1、必须是正整数
     * 2、必须是2的次方数
     * 3、不能出现重复
     * </p>
     *
     * @param targets 可枚举的指令组
     * @param <T>     任意指令类型
     * @return 验证结果 -1 无效 否则 有效
     */
    public static <T extends Directive> int verificationDefinition(T[] targets) {
        int sum = 0;
        for (T target : targets) {
            int intValue = target.intValue();
            if (!isBitValue(intValue) || (intValue & sum) != 0) {
                return -1;
            }
            sum = sum + intValue;
        }
        return sum;
    }

    /**
     * <h>验证指令组有效性</h>
     * <p>
     * 1、必须是正整数
     * 2、必须是2的次方数
     * 3、不能出现重复
     * </p>
     *
     * @param targets 可迭代的指令组
     * @param <T>     任意指令类型
     * @return 验证结果 -1 无效 否则 有效
     */
    public static <T extends Directive> int verificationDefinition(Iterable<T> targets) {
        int sum = 0;
        for (T target : targets) {
            int intValue = target.intValue();
            if (!isBitValue(intValue) || (intValue & sum) != 0) {
                return -1;
            }
            sum = sum + intValue;
        }
        return sum;
    }

    /**
     * 验证可执行性
     *
     * @param origin  执行源
     * @param targets 可迭代的指令组
     * @param <T>     任意指令类型
     * @return 是否可执行
     */
    public static <T extends Directive> boolean verificationExecutable(int origin, T[] targets) {
        int verificationResult = verificationDefinition(targets);
        return verificationResult != -1 && verificationResult > origin;
    }

    /**
     * 验证可执行性
     *
     * @param origin  执行源
     * @param targets 可枚举的指令组
     * @param <T>     任意指令类型
     * @return 是否可执行
     */
    public static <T extends Directive> boolean verificationExecutable(int origin, Iterable<T> targets) {
        int verificationResult = verificationDefinition(targets);
        return verificationResult != -1 && verificationResult > origin;
    }

    /**
     * 指令执行
     *
     * @param origin   执行源
     * @param targets  可枚举的指令组
     * @param consumer 指令执行函数
     * @param <T>      任意指令类型
     */
    public static <T extends Directive> int execute(int origin, T[] targets, Consumer<T> consumer) {
        for (T target : targets) {
            int intValue = target.intValue();
            if ((origin & intValue) == intValue) {
                consumer.accept(target);
                origin = disable(origin, target);
                if (origin == 0) {
                    break;
                }
            }
        }
        return origin;
    }

    /**
     * 指令执行
     *
     * @param origin   执行源
     * @param targets  可迭代的指令组
     * @param consumer 指令执行函数
     * @param <T>      任意指令类型
     */
    public static <T extends Directive> int execute(int origin, Iterable<T> targets, Consumer<T> consumer) {
        for (T target : targets) {
            int intValue = target.intValue();
            if ((origin & intValue) == intValue) {
                consumer.accept(target);
                origin = disable(origin, target);
                if (origin == 0) {
                    break;
                }
            }
        }
        return origin;
    }
}

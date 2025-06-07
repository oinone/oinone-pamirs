package pro.shushi.pamirs.middleware.schedule.directive;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 14:47
 */
public class DirectiveHelper {

    public static <T extends Directive> int enable(int origin, T target) {
        return origin | target.intValue();
    }

    public static <T extends Directive> int disable(int origin, T target) {
        return origin & ~target.intValue();
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
        return (origin & target.intValue()) != 0;
    }

    public static <T extends Directive> boolean isDisabled(int origin, T target) {
        return (origin & target.intValue()) == 0;
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
            if (intValue <= 0 || (intValue & intValue - 1) != 0 || (intValue & sum) != 0) {
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
            if (intValue <= 0 || (intValue & intValue - 1) != 0 || (intValue & sum) != 0) {
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
        if (verificationResult == -1 || verificationResult <= origin) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
        if (verificationResult == -1 || verificationResult <= origin) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 指令执行
     *
     * @param origin   执行源
     * @param targets  可枚举的指令组
     * @param consumer 指令执行函数
     * @param <T>      任意指令类型
     */
    public static <T extends Directive> void execute(int origin, T[] targets, Consumer<T> consumer) {
        int i = 0;
        do {
            if ((origin & 1) == 0) {
                consumer.accept(targets[i]);
            }
            i++;
            origin = origin >> 1;
        } while (origin != 0);
    }

    /**
     * 指令执行
     *
     * @param origin   执行源
     * @param targets  可迭代的指令组
     * @param consumer 指令执行函数
     * @param <T>      任意指令类型
     */
    public static <T extends Directive> void execute(int origin, Iterable<T> targets, Consumer<T> consumer) {
        Iterator<T> iterator = targets.iterator();
        do {
            T directive = iterator.next();
            if ((origin & 1) == 0) {
                consumer.accept(directive);
            }
            origin = origin >> 1;
        } while (origin != 0);
    }
}

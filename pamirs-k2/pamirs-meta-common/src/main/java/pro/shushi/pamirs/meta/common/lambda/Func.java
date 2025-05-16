package pro.shushi.pamirs.meta.common.lambda;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * 函数式接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@FunctionalInterface
public interface Func<T, P, R> extends BiFunction<T, P, R>, Serializable {

}
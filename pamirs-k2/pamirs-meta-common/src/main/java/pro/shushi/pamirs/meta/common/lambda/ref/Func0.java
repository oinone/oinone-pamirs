package pro.shushi.pamirs.meta.common.lambda.ref;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 无参函数式接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@FunctionalInterface
public interface Func0<T, R> extends Function<T, R>, Serializable {

}
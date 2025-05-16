package pro.shushi.pamirs.meta.common.lambda.ref;

import java.io.Serializable;

/**
 * 2个参数的函数式接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@FunctionalInterface
public interface Func2<T, A1, A2, R> extends Serializable {
    R apply(T instance, A1 arg1, A2 arg2) throws Exception;
}
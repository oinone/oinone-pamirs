package pro.shushi.pamirs.meta.common.lambda.ref;

import java.io.Serializable;

/**
 * 4个参数的函数式接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@FunctionalInterface
public interface Func4<T, A1, A2, A3, A4, R> extends Serializable {
    R apply(T instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws Exception;
}
package pro.shushi.pamirs.meta.common.lambda;

import pro.shushi.pamirs.meta.common.lambda.ref.Func0;

import java.io.Serializable;

/**
 * Getter函数式接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/19 1:39 下午
 */
@FunctionalInterface
public interface Getter<T, R> extends Func0<T, R>, Serializable {

}
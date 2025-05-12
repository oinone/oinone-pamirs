package pro.shushi.pamirs.meta.common.lambda;

/**
 * Lambda 快速方法
 * <p>
 * 2021/4/28 9:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class F {

    public static <T, R> String f(Getter<T, R> fn) {
        return LambdaUtil.fetchFieldName(fn);
    }

}

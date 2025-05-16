package pro.shushi.pamirs.eip.api;

@FunctionalInterface
public interface IEipExceptionPredict<T> {

    /**
     * @param context 上下文
     * @return 返回false时，表示无异常；反之则有异常；
     */
    boolean test(IEipContext<T> context);
}

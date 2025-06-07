package pro.shushi.pamirs.eip.api;

@FunctionalInterface
public interface IEipContextSupplier<T> {

    IEipContext<T> get(IEipApi eipApi, T executorContext, T interfaceContext);
}

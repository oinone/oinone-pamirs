package pro.shushi.pamirs.meta.common.logger;

public interface IThrowExceptionBuilder<E extends IThrowException> {

    IThrowExceptionBuilder<E> appendMsg(String otherMsg);

    IThrowExceptionBuilder<E> setExtendObject(Object object);

    E errThrow();
}

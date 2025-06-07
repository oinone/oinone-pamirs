package pro.shushi.pamirs.file.api.builder;

public abstract class AbstractBaseBuilder<T> {

    protected final T builder;

    public AbstractBaseBuilder(T builder) {
        this.builder = builder;
    }

    public T and() {
        return builder;
    }
}

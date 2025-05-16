package pro.shushi.pamirs.eip.api.builder;

public abstract class AbstractBaseBuilder<T> {

    private final AbstractEipInterfaceBuilder<T> interfaceBuilder;

    public AbstractBaseBuilder(AbstractEipInterfaceBuilder<T> interfaceBuilder) {
        this.interfaceBuilder = interfaceBuilder;
    }

    public AbstractEipInterfaceBuilder<T> and() {
        return interfaceBuilder;
    }
}

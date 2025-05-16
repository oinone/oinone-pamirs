package pro.shushi.pamirs.core.common.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-12-18 11:48
 */
public abstract class AbstractDirective<T> extends AbstractExecuteDirective implements Directive {

    private final T service;

    protected AbstractDirective(int intValue, T service) {
        super(intValue);
        this.service = service;
    }

    public T getService() {
        return service;
    }
}

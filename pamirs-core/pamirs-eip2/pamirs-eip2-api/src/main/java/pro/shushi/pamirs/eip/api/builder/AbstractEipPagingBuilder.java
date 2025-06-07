package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.IEipPagingPredict;
import pro.shushi.pamirs.eip.api.IEipPagingProcessor;

import java.util.function.Function;

public abstract class AbstractEipPagingBuilder<T> extends AbstractBaseBuilder<T> {

    protected Integer pageSize;

    protected Integer startPage;

    protected Integer endPage;

    protected Function<IEipIntegrationInterface<T>, IEipPagingProcessor<T>> processor;

    protected IEipPagingPredict<T> predict;

    public AbstractEipPagingBuilder(AbstractEipInterfaceBuilder<T> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public AbstractEipPagingBuilder<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public AbstractEipPagingBuilder<T> setStartPage(Integer startPage) {
        this.startPage = startPage;
        return this;
    }

    public AbstractEipPagingBuilder<T> setEndPage(Integer endPage) {
        this.endPage = endPage;
        return this;
    }

    public AbstractEipPagingBuilder<T> setProcessor(Function<IEipIntegrationInterface<T>, IEipPagingProcessor<T>> processor) {
        this.processor = processor;
        return this;
    }

    public AbstractEipPagingBuilder<T> setPredict(IEipPagingPredict<T> predict) {
        this.predict = predict;
        return this;
    }

    public IEipPaging<T> build(IEipIntegrationInterface<T> eipInterface) {
        return build0(eipInterface).afterProperty();
    }

    protected abstract IEipPaging<T> build0(IEipIntegrationInterface<T> eipInterface);
}

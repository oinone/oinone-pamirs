package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.IEipPagingPredict;
import pro.shushi.pamirs.eip.api.IEipPagingProcessor;

public abstract class AbstractEipPaging<T> implements IEipPaging<T> {

    private Integer pageSize;

    private Integer startPage;

    private Integer endPage;

    private IEipPagingProcessor<T> processor;

    private IEipPagingPredict<T> predict;

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    public AbstractEipPaging<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public Integer getStartPage() {
        return startPage;
    }

    public AbstractEipPaging<T> setStartPage(Integer startPage) {
        this.startPage = startPage;
        return this;
    }

    @Override
    public Integer getEndPage() {
        return endPage;
    }

    public AbstractEipPaging<T> setEndPage(Integer endPage) {
        this.endPage = endPage;
        return this;
    }

    @Override
    public IEipPagingProcessor<T> getProcessor() {
        return processor;
    }

    public AbstractEipPaging<T> setProcessor(IEipPagingProcessor<T> processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public IEipPagingPredict<T> getPredict() {
        return predict;
    }

    public AbstractEipPaging<T> setPredict(IEipPagingPredict<T> predict) {
        this.predict = predict;
        return this;
    }

    @Override
    public IEipPaging<T> afterProperty() {
        if (this.pageSize == null)
            this.pageSize = 2000;
        if (this.startPage == null || this.startPage < 0)
            this.startPage = 0;
        if (this.endPage == null || this.endPage < 0)
            this.endPage = 0;
        if (this.predict == null)
            this.predict = getDefaultPredict();
        return this;
    }

    protected abstract IEipPagingPredict<T> getDefaultPredict();
}

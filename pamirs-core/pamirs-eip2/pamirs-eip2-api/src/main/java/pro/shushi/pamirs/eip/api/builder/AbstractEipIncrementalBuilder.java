package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.eip.api.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEipIncrementalBuilder<T> extends AbstractBaseBuilder<T> {

    protected IEipConverter<T> converter;

    protected IEipIncrementalParamConverter<T> incrementalParamConverter;

    protected IEipIncrementalParamConverterCallback<T> incrementalParamConverterCallback;

    protected List<IEipIncrementalParam> incrementalParamList;

    public IEipConverter<T> getConverter() {
        return converter;
    }

    public AbstractEipIncrementalBuilder<T> setConverter(IEipConverter<T> converter) {
        this.converter = converter;
        return this;
    }

    public AbstractEipIncrementalBuilder<T> setIncrementalParamConverter(IEipIncrementalParamConverter<T> incrementalParamConverter) {
        this.incrementalParamConverter = incrementalParamConverter;
        return this;
    }

    public AbstractEipIncrementalBuilder<T> setIncrementalParamConverterCallback(IEipIncrementalParamConverterCallback<T> incrementalParamConverterCallback) {
        this.incrementalParamConverterCallback = incrementalParamConverterCallback;
        return this;
    }

    public List<IEipIncrementalParam> getIncrementalParamList() {
        return incrementalParamList;
    }

    public AbstractEipIncrementalBuilder<T> setIncrementalParamList(List<IEipIncrementalParam> incrementalParamList) {
        this.incrementalParamList = incrementalParamList;
        return this;
    }

    public AbstractEipIncrementalBuilder<T> addIncrementalParam(IEipIncrementalParam incrementalParam) {
        if (this.incrementalParamList == null)
            this.incrementalParamList = new ArrayList<>();
        this.incrementalParamList.add(incrementalParam);
        return this;
    }

    public AbstractEipIncrementalBuilder(AbstractEipInterfaceBuilder<T> interfaceBuilder) {
        super(interfaceBuilder);
    }

    public IEipIncrementalProcessor<T> build() {
        return build0();
    }

    protected abstract IEipIncrementalProcessor<T> build0();
}

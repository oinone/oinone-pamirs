package pro.shushi.pamirs.framework.gateways.graph.java.strategy.fetcher;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

/**
 * 重写 GQL PropertyDataFetcher, 使用 _d 快速获取属性值
 *
 * @author Adamancy Zhang at 10:16 on 2024-07-12
 */
public class PamirsPropertyDataFetcher<T> extends PropertyDataFetcher<T> {

    public PamirsPropertyDataFetcher(String propertyName) {
        super(propertyName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(DataFetchingEnvironment environment) {
        Object source = environment.getSource();
        if (source == null) {
            return null;
        }
        return (T) PamirsPropertyDataFetcherHelper.getPropertyValue(getPropertyName(), source, environment.getFieldType(), environment);
    }
}

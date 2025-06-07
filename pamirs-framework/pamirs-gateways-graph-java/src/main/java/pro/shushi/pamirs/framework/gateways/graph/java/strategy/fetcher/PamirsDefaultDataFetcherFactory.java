package pro.shushi.pamirs.framework.gateways.graph.java.strategy.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.DataFetcherFactoryEnvironment;

/**
 * 默认DataFetcher工厂, 替换 GQL 默认PropertyDataFetcher为PamirsPropertyDataFetcher
 *
 * @author Adamancy Zhang at 12:24 on 2024-07-12
 */
public class PamirsDefaultDataFetcherFactory<T> implements DataFetcherFactory<T> {

    public static final DataFetcherFactory<?> INSTANCE = new PamirsDefaultDataFetcherFactory<>();

    @Override
    public DataFetcher<T> get(DataFetcherFactoryEnvironment environment) {
        return new PamirsPropertyDataFetcher<>(environment.getFieldDefinition().getName());
    }
}

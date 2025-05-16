package pro.shushi.pamirs.framework.gateways.graph.java.request;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.configuration.PamirsFrameworkGatewayConfiguration;
import pro.shushi.pamirs.framework.gateways.graph.java.executor.ExecutorServiceApi;
import pro.shushi.pamirs.framework.gateways.graph.spi.ActionBinderApi;
import pro.shushi.pamirs.framework.gateways.graph.spi.DataLoaderRegistryApi;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 默认数据加载器注册实现
 * <p>
 * 2021/3/29 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultDataLoaderRegistryApi implements DataLoaderRegistryApi {

    private static final HoldKeeper<Boolean> isAsync = new HoldKeeper<>();

    private static boolean isAsync() {
        return isAsync.supply(() -> BeanDefinitionUtils.getBean(PamirsFrameworkGatewayConfiguration.class).isAsync());
    }

    @Override
    public DataLoaderRegistry dataLoader() {
        ActionBinderApi actionBinderApi = Spider.getDefaultExtension(ActionBinderApi.class);

        BatchLoaderWithContext<String, Object> commonBatchLoader;
        if (isAsync()) {
            commonBatchLoader = (keys, keyContexts) -> CompletableFuture.supplyAsync(() -> relationQuery(actionBinderApi, keys, keyContexts), ExecutorServiceApi.getExecutorService());
        } else {
            commonBatchLoader = (keys, keyContexts) -> CompletableFuture.completedFuture(relationQuery(actionBinderApi, keys, keyContexts));
        }

        DataLoader<String, Object> commonDataLoader = DataLoader.newDataLoader(commonBatchLoader);

        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register(COMMON_DATA_LOADER, commonDataLoader);
        return registry;
    }

    private List<Object> relationQuery(ActionBinderApi actionBinderApi, List<String> keys, BatchLoaderEnvironment keyContexts) {
        try {
            return Models.directive().request(() -> actionBinderApi.relationQuery(keys, keyContexts));
        } finally {
            PamirsSession.clearSubSession();
        }
    }
}

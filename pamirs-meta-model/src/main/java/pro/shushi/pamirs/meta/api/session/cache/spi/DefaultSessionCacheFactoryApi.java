package pro.shushi.pamirs.meta.api.session.cache.spi;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.cache.CacheProxy;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.api.*;
import pro.shushi.pamirs.meta.api.session.cache.local.*;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工厂API默认实现
 * <p>
 * 2021/8/20 12:57 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
@Component
public class DefaultSessionCacheFactoryApi implements SessionCacheFactoryApi {

    private static final Cache<String, Function> functionCache = CacheProxy.getInstance(
            Caffeine.newBuilder().maximumSize(100_000).expireAfterWrite(10, TimeUnit.SECONDS).build()
    );

    private static final Cache<String, Function> functionByNameCache = CacheProxy.getInstance(
            Caffeine.newBuilder().maximumSize(100_000).expireAfterWrite(10, TimeUnit.SECONDS).build()
    );

    @Override
    public ModuleCacheApi fetchModuleCache() {
        ModuleCacheApi moduleCacheApi = new ModuleCache();
        moduleCacheApi.init();
        return moduleCacheApi;
    }

    @Override
    public ModelCacheApi fetchModelCache() {
        ModelCacheApi modelCacheApi = new ModelCache();
        modelCacheApi.init();
        return modelCacheApi;
    }

    @Override
    public SequenceConfigCacheApi fetchSequenceConfigCache() {
        SequenceConfigCacheApi sequenceConfigCacheApi = new SequenceConfigCache();
        sequenceConfigCacheApi.init();
        return sequenceConfigCacheApi;
    }

    @Override
    public StandaloneFunCacheApi fetchStandaloneFunCache() {
        StandaloneFunCacheApi standaloneFunCacheApi = new StandaloneFunCache();
        standaloneFunCacheApi.init();
        return standaloneFunCacheApi;
    }

    @Override
    public TxConfigCacheApi fetchTxConfigCache() {
        TxConfigCacheApi txConfigCacheApi = new TxConfigCache();
        txConfigCacheApi.init();
        return txConfigCacheApi;
    }

    @Override
    public DataDictionaryCacheApi fetchDataDictionaryCache() {
        DataDictionaryCacheApi dataDictionaryCacheApi = new DataDictionaryCache();
        dataDictionaryCacheApi.init();
        return dataDictionaryCacheApi;
    }

    @Override
    public InterfaceCacheApi fetchInterfaceCache() {
        InterfaceCacheApi interfaceCacheApi = new InterfaceCache();
        interfaceCacheApi.init();
        return interfaceCacheApi;
    }

    @Override
    public HooksCacheApi fetchHookCache() {
        HooksCache hooksCache = new HooksCache();
        hooksCache.init();
        return hooksCache;
    }

    @Override
    public ExtPointImplementationCacheApi fetchExtPointImplementationCache() {
        ExtPointImplementationCacheApi extPointImplementationCacheApi = new ExtPointImplementationCache();
        extPointImplementationCacheApi.init();
        return extPointImplementationCacheApi;
    }

    @Override
    public ExpressionDefinitionCacheApi fetchExpressionDefinitionCache() {
        ExpressionDefinitionCacheApi expressionDefinitionCacheApi = new ExpressionDefinitionCache();
        expressionDefinitionCacheApi.init();
        return expressionDefinitionCacheApi;
    }

    @Override
    public ComputeDefinitionCacheApi fetchComputeDefinitionCache() {
        ComputeDefinitionCacheApi computeDefinitionCacheApi = new ComputeDefinitionCache();
        computeDefinitionCacheApi.init();
        return computeDefinitionCacheApi;
    }

    @Override
    public Map<String, Object> fetchExtendCache() {
        Map<String, Object> cache = new ConcurrentHashMap<>();
        for (ExtendCacheInitApi extendCacheInitApi : Spider.getLoader(ExtendCacheInitApi.class).getOrderedExtensions()) {
            extendCacheInitApi.init(cache);
        }
        return cache;
    }

    @Override
    public Cache<String, Function> fetchFunctionCache() {
        return functionCache;
    }

    @Override
    public Cache<String, Function> fetchFunctionByNameCache() {
        return functionByNameCache;
    }
}

package pro.shushi.pamirs.meta.api.session.cache.spi;

import com.github.benmanes.caffeine.cache.Cache;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.api.*;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 缓存工厂API
 * <p>
 * 2021/8/20 12:57 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionCacheFactoryApi {

    ModuleCacheApi fetchModuleCache();

    ModelCacheApi fetchModelCache();

    SequenceConfigCacheApi fetchSequenceConfigCache();

    StandaloneFunCacheApi fetchStandaloneFunCache();

    TxConfigCacheApi fetchTxConfigCache();

    DataDictionaryCacheApi fetchDataDictionaryCache();

    InterfaceCacheApi fetchInterfaceCache();

    HooksCacheApi fetchHookCache();

    ExtPointImplementationCacheApi fetchExtPointImplementationCache();

    ExpressionDefinitionCacheApi fetchExpressionDefinitionCache();

    ComputeDefinitionCacheApi fetchComputeDefinitionCache();

    Map<String, Object> fetchExtendCache();

    Cache<String, Function> fetchFunctionCache();

    Cache<String, Function> fetchFunctionByNameCache();

}

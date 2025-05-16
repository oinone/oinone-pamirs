package pro.shushi.pamirs.meta.api.session.cache;

import com.github.benmanes.caffeine.cache.Cache;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.cache.api.*;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;

import java.util.Map;

/**
 * session缓存工厂
 * <p>
 * 2021/8/19 12:59 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SessionCacheFactory {

    public static ModuleCacheApi fetchModuleCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchModuleCache();
    }

    public static ModelCacheApi fetchModelCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchModelCache();
    }

    public static SequenceConfigCacheApi fetchSequenceConfigCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchSequenceConfigCache();
    }

    public static StandaloneFunCacheApi fetchStandaloneFunCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchStandaloneFunCache();
    }

    public static TxConfigCacheApi fetchTxConfigCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchTxConfigCache();
    }

    public static DataDictionaryCacheApi fetchDataDictionaryCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchDataDictionaryCache();
    }

    public static InterfaceCacheApi fetchInterfaceCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchInterfaceCache();
    }

    public static HooksCacheApi fetchHookCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchHookCache();
    }

    public static ExtPointImplementationCacheApi fetchExtPointImplementationCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchExtPointImplementationCache();
    }

    public static ExpressionDefinitionCacheApi fetchExpressionDefinitionCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchExpressionDefinitionCache();
    }

    public static ComputeDefinitionCacheApi fetchComputeDefinitionCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchComputeDefinitionCache();
    }

    public static Cache<String, Function> fetchFunctionCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchFunctionCache();
    }

    public static Map<String, Object> fetchExtendCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchExtendCache();
    }

    public static Cache<String, Function> fetchFunctionByNameCache(SessionCacheFactoryApi sessionCacheFactoryApi) {
        return sessionCacheFactoryApi.fetchFunctionByNameCache();
    }

}

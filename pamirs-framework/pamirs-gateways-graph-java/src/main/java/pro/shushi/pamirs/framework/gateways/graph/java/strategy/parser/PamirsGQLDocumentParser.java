package pro.shushi.pamirs.framework.gateways.graph.java.strategy.parser;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import graphql.ExecutionInput;
import graphql.ParseAndValidate;
import graphql.ParseAndValidateResult;
import pro.shushi.pamirs.framework.common.utils.PropertyHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Holder;

import java.util.concurrent.TimeUnit;

/**
 * PamirsGQL解析（带缓存）
 *
 * @author Adamancy Zhang at 18:31 on 2024-07-13
 */
@Slf4j
public class PamirsGQLDocumentParser {

    /**
     * 持久化缓存
     */
    private static final Cache<String, CacheResult> persistentCache;

    /**
     * 计数缓存
     */
    private static final Cache<String, CacheResult> countCache;

    /**
     * 计数缓存晋升持久化命中次数
     */
    private static final int DOCUMENT_CACHE_PROMOTION_COUNT;

    static {
        int documentCacheCount = PropertyHelper.getIntProperty("pamirs.global.gql.document-cache.count", 10_000);
        int documentCacheExpireAfterWrite = PropertyHelper.getIntProperty("pamirs.global.gql.document-cache.expire-after-write", 600);
        int documentPersistentCacheCount = PropertyHelper.getIntProperty("pamirs.global.gql.document-cache.persistent-count", 1_000);
        DOCUMENT_CACHE_PROMOTION_COUNT = PropertyHelper.getIntProperty("pamirs.global.gql.document-cache.promotion-count", 100);
        if (documentCacheCount <= 0 || documentPersistentCacheCount <= 0) {
            countCache = null;
            persistentCache = null;
        } else {
            countCache = Caffeine.newBuilder().maximumSize(documentCacheCount).expireAfterWrite(documentCacheExpireAfterWrite, TimeUnit.SECONDS).build();
            persistentCache = Caffeine.newBuilder().maximumSize(documentPersistentCacheCount).build();
        }
    }

    public static ParseAndValidateResult getParseResult(ExecutionInput executionInput) {
        if (countCache == null) {
            return parse(executionInput);
        }
        Holder<CacheResult> holder = new Holder<>();
        CacheResult cacheResult = persistentCache.get(executionInput.getQuery(), (query) -> {
            CacheResult level1Cache = countCache.get(query, (k) -> new CacheResult(parse(executionInput)));
            if (level1Cache == null) {
                return null;
            }
            if (level1Cache.countDown() >= DOCUMENT_CACHE_PROMOTION_COUNT) {
                countCache.invalidate(query);
                return level1Cache;
            }
            holder.set(level1Cache);
            return null;
        });
        if (cacheResult == null) {
            cacheResult = holder.get();
        }
        return cacheResult.getResult();
    }

    private static ParseAndValidateResult parse(ExecutionInput executionInput) {
        if (log.isDebugEnabled()) {
            log.debug("Parsing query: '{}'...", executionInput.getQuery());
        }
        return ParseAndValidate.parse(executionInput);
    }

    private static class CacheResult {

        private final ParseAndValidateResult result;

        private int counter;

        public CacheResult(ParseAndValidateResult result) {
            this.result = result;
            this.counter = -1;
        }

        public ParseAndValidateResult getResult() {
            return result;
        }

        public int countDown() {
            return counter++;
        }
    }
}

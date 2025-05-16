package pro.shushi.pamirs.framework.faas.script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_SCRIPT_CACHE_ERROR;

/**
 * Groovy脚本缓存
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class GroovyCache {

    private static Cache<String, Optional<Object>> localMemoryCache =
            CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).build();

    public static String GROOVY_SHELL_KEY_PREFIX = "GROOVY_SHELL#";

    protected static <T> T getValue(String key, Callable<Optional<Object>> load) {

        try {
            Optional<Object> value = localMemoryCache.get(key, load);
            //noinspection unchecked
            return (T) value.orElse(null);
        } catch (Exception ex) {
            log.error("获取缓存异常,key:{} ", key, ex);
            throw PamirsException.construct(BASE_SCRIPT_CACHE_ERROR, ex).errThrow();
        }

    }

}

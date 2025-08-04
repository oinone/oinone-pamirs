package pro.shushi.pamirs.meta.api.session.cache.fast;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 模型字段线程级别缓存
 *
 * @author Adamancy Zhang at 10:48 on 2024-10-17
 */
@Component
public class ModelFieldThreadCache implements SessionClearApi {

    private static final TransmittableThreadLocal<Map<String, ModelFieldConfig>> CACHE_BY_FIELD = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, ModelFieldConfig>> CACHE_BY_NAME = new TransmittableThreadLocal<>();

    public static ModelFieldConfig get(String model, String field, BiFunction<String, String, ModelFieldConfig> supplier) {
        return init().computeIfAbsent(model + CharacterConstants.SEPARATOR_OCTOTHORPE + field, k -> supplier.apply(model, field));
    }

    public static ModelFieldConfig getByName(String model, String name, BiFunction<String, String, ModelFieldConfig> supplier) {
        return initByName().computeIfAbsent(model + CharacterConstants.SEPARATOR_OCTOTHORPE + name, k -> supplier.apply(model, name));
    }

    private static Map<String, ModelFieldConfig> init() {
        Map<String, ModelFieldConfig> cached = CACHE_BY_FIELD.get();
        if (cached == null) {
            cached = new ConcurrentHashMap<>();
            CACHE_BY_FIELD.set(cached);
        }
        return cached;
    }

    private static Map<String, ModelFieldConfig> initByName() {
        Map<String, ModelFieldConfig> cached = CACHE_BY_NAME.get();
        if (cached == null) {
            cached = new ConcurrentHashMap<>();
            CACHE_BY_NAME.set(cached);
        }
        return cached;
    }

    @Override
    public void clear() {
        CACHE_BY_FIELD.remove();
        CACHE_BY_NAME.remove();
    }
}

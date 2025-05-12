package pro.shushi.pamirs.meta.api.session.cache.api;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.cache.SessionCache;
import pro.shushi.pamirs.meta.api.session.cache.SessionCacheInitApi;

import java.util.List;

/**
 * 模型缓存接口
 *
 * <p>key为model
 * 2021/8/19 2:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModelCacheApi extends SessionCache<ModelConfig>, SessionCacheInitApi {

    boolean isEmpty();

    ModelConfig getByName(String name);

    List<String> getModelsByTable(String table);

    default ModelConfig getSimpleModelConfig(String model) {
        return get(model);
    }

    default ModelConfig getSimpleModelConfigByName(String name) {
        return getByName(name);
    }

}

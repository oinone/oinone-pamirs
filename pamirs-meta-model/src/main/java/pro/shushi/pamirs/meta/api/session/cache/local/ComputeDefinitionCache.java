package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.TripleFunction;
import pro.shushi.pamirs.meta.api.session.cache.api.ComputeDefinitionCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForPutAll;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;

import java.util.List;
import java.util.Map;

/**
 * 计算函数缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ComputeDefinitionCache extends AbstractCacheWithTripleKey<List<ComputeDefinition>>
        implements ComputeDefinitionCacheApi, SessionCacheForPutAll<List<ComputeDefinition>> {

    @Override
    public String type() {
        return ComputeDefinition.class.getSimpleName();
    }

    @Override
    public TripleFunction<String, String, String, String> keyGenerator() {
        return ExpressionDefinition::key;
    }

    @Override
    public void putAll(Map<String, List<ComputeDefinition>> map) {
        getMap().putAll(map);
    }

}

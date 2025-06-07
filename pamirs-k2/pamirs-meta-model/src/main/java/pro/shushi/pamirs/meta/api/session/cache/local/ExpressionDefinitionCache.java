package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.TripleFunction;
import pro.shushi.pamirs.meta.api.session.cache.api.ExpressionDefinitionCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForPutAll;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;

import java.util.List;
import java.util.Map;

/**
 * 表达式定义缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ExpressionDefinitionCache extends AbstractCacheWithTripleKey<List<ExpressionDefinition>>
        implements ExpressionDefinitionCacheApi, SessionCacheForPutAll<List<ExpressionDefinition>> {

    @Override
    public String type() {
        return ExpressionDefinition.class.getSimpleName();
    }

    @Override
    public TripleFunction<String, String, String, String> keyGenerator() {
        return ExpressionDefinition::key;
    }

    @Override
    public void putAll(Map<String, List<ExpressionDefinition>> map) {
        getMap().putAll(map);
    }

}

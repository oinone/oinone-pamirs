package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.api.DataDictionaryCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForKeySet;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

import java.util.Set;

/**
 * 数据字典缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataDictionaryCache extends AbstractCache<DataDictionary> implements DataDictionaryCacheApi, SessionCacheForKeySet {

    @Override
    public String type() {
        return DataDictionary.class.getSimpleName();
    }

    @Override
    public Set<String> keySet() {
        return getMap().keySet();
    }
}

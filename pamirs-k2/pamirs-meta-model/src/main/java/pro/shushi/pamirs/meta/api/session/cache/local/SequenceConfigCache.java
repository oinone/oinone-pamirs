package pro.shushi.pamirs.meta.api.session.cache.local;

import pro.shushi.pamirs.meta.api.session.cache.api.SequenceConfigCacheApi;
import pro.shushi.pamirs.meta.api.session.cache.extend.SessionCacheForPutAll;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

import java.util.Map;

/**
 * 数据编码缓存
 * <p>
 * 2021/8/19 12:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SequenceConfigCache extends AbstractCache<SequenceConfig>
        implements SequenceConfigCacheApi, SessionCacheForPutAll<SequenceConfig> {

    @Override
    public String type() {
        return SequenceConfig.class.getSimpleName();
    }

    @Override
    public void putAll(Map<String, SequenceConfig> map) {
        this.getMap().putAll(map);
    }
}

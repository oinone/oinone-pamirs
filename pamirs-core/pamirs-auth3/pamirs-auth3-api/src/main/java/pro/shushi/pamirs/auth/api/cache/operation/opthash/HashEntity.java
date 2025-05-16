package pro.shushi.pamirs.auth.api.cache.operation.opthash;

import java.util.Map;

/**
 * Hash类型缓存实体
 *
 * @author Adamancy Zhang at 17:05 on 2024-01-10
 */
public class HashEntity<HK, HV> {

    private final String key;

    private final Map<HK, HV> cacheHash;

    public HashEntity(String key, Map<HK, HV> cacheHash) {
        this.key = key;
        this.cacheHash = cacheHash;
    }

    public String getKey() {
        return key;
    }

    public Map<HK, HV> getCacheHash() {
        return cacheHash;
    }
}

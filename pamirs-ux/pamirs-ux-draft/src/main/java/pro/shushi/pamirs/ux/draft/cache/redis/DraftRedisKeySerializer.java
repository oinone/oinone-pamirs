package pro.shushi.pamirs.ux.draft.cache.redis;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;
import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 草稿缓存Key序列化
 *
 * @author Adamancy Zhang at 12:23 on 2025-10-21
 */
public class DraftRedisKeySerializer extends PamirsStringRedisSerializer {

    public DraftRedisKeySerializer(PamirsStringRedisSerializer serializer) {
//        super(generatorPrefix(serializer.getPrefix()));
        super(serializer.getPrefix());
    }

    private static String generatorPrefix(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return DraftConstants.CACHE_KEY_PREFIX;
        }
        return prefix + CharacterConstants.SEPARATOR_COLON + DraftConstants.CACHE_KEY_PREFIX;
    }
}

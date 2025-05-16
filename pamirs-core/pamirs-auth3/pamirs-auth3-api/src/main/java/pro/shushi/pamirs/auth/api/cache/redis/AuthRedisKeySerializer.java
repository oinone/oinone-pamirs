package pro.shushi.pamirs.auth.api.cache.redis;

import pro.shushi.pamirs.framework.connectors.data.serializer.PamirsStringRedisSerializer;

/**
 * 权限缓存Key序列化
 *
 * @author Adamancy Zhang at 20:11 on 2024-01-29
 */
public class AuthRedisKeySerializer extends PamirsStringRedisSerializer {

    public AuthRedisKeySerializer(PamirsStringRedisSerializer serializer) {
        super(serializer.getPrefix());
    }
}

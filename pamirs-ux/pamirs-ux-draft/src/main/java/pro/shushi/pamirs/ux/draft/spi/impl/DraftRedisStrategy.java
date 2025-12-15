package pro.shushi.pamirs.ux.draft.spi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.ux.draft.cache.redis.DraftRedisTemplate;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;
import pro.shushi.pamirs.ux.draft.model.Draft;
import pro.shushi.pamirs.ux.draft.spi.DraftStrategyApi;
import pro.shushi.pamirs.framework.common.serialize.KryoSerializer;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.concurrent.TimeUnit;

/**
 * Redis存储策略
 *
 * @author Adamancy Zhang at 15:37 on 2025-10-20
 */
@Component
@SPI.Service(DraftConstants.REDIS_STORAGE)
public class DraftRedisStrategy extends AbstractDraftStrategy implements DraftStrategyApi {

    @Autowired
    @Qualifier(DraftConstants.REDIS_TEMPLATE_BEAN_NAME)
    private DraftRedisTemplate<Draft> draftRedisTemplate;

    @Override
    protected String getType() {
        return DraftConstants.REDIS_STORAGE;
    }

    @Override
    public Draft get(Object data) {
        Draft draft = load(data);
        if (draft == null) {
            return null;
        }
        return draftRedisTemplate.opsForValue().get(DraftConstants.CACHE_KEY_PREFIX + getUserId(draft) + CharacterConstants.SEPARATOR_COLON + draft.getCode());
    }

    @Override
    public Draft getByWrapper(IWrapper<?> wrapper) {
        Object data = Models.data().queryOneByWrapper(wrapper);
        if (data == null) {
            return null;
        }
        return get(data);
    }

    @Override
    public Draft create(Draft draft) {
        long now = System.currentTimeMillis() / 1000;
        Long invalidDate = getInvalidDate(now, draft);
        String key = DraftConstants.CACHE_KEY_PREFIX + getUserId(draft) + CharacterConstants.SEPARATOR_COLON + draft.getCode();
        draftRedisTemplate.opsForValue().set(key, draft);
        if (invalidDate != null) {
            long timeout = invalidDate - now;
            draftRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
        return draft;
    }

    @Override
    public Draft update(Draft draft) {
        return create(draft);
    }

    @Override
    public boolean delete(String draftCode) {
        return Boolean.TRUE.equals(draftRedisTemplate.delete(DraftConstants.CACHE_KEY_PREFIX + getUserId() + CharacterConstants.SEPARATOR_COLON + draftCode));
    }

    @Override
    public <T> Object serialization(String model, T data) {
        return KryoSerializer.serialize(data);
    }

    @Override
    public <T> T deserialization(String model, Object draftData) {
        return KryoSerializer.deserialize((byte[]) draftData);
    }

    protected String getUserId() {
        String userId;
        Object userIdObject = PamirsSession.getUserId();
        if (userIdObject == null) {
            userId = DraftConstants.ANONYMOUS_USER;
        } else {
            userId = String.valueOf(userIdObject);
        }
        return userId;
    }

    protected String getUserId(Draft draft) {
        String userId = draft.getUserId();
        if (userId == null) {
            userId = getUserId();
        }
        return userId;
    }
}

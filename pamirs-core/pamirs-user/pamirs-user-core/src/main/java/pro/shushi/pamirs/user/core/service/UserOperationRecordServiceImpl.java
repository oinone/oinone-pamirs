package pro.shushi.pamirs.user.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.user.api.constants.UserConstant;
import pro.shushi.pamirs.user.api.service.UserOperationRecordService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author shier
 * date  2022/6/1 下午4:57
 */
@Component
public class UserOperationRecordServiceImpl implements UserOperationRecordService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Integer recordLoginErrorCount(String login) {
        String key = generateLoginErrorCountKey(login);
        List<Object> result = redisTemplate.executePipelined(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().increment(key);
                operations.expire(key, 60, TimeUnit.SECONDS);
                return null;
            }
        });
        Long errorRecords = null;
        if (result.size() >= 1) {
            errorRecords = (Long) result.get(0);
        }
        return Optional.ofNullable(errorRecords)
                .filter(_errorRecords -> _errorRecords < Integer.MAX_VALUE)
                .map(_err -> _err.intValue())
                .orElse(Integer.MAX_VALUE);
    }

    @Override
    public Integer getLoginErrorCount(String login) {
        String key = generateLoginErrorCountKey(login);
        String errorCount = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(errorCount).map(_errorCountStr -> Integer.valueOf(_errorCountStr)).orElse(0);
    }


    public String generateLoginErrorCountKey(String code) {
        String tenant = Optional.ofNullable(PamirsTenantSession.getTenant()).orElse(StringUtils.EMPTY);
        return tenant + CharacterConstants.SEPARATOR_DOLLAR + code + CharacterConstants.SEPARATOR_DOLLAR + UserConstant.LOGIN_ERROR_COUNT;
    }
}

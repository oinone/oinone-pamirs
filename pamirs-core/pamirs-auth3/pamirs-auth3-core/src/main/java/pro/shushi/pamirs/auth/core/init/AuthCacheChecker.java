package pro.shushi.pamirs.auth.core.init;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import pro.shushi.pamirs.auth.api.service.manager.AuthCacheManager;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.TimeWatcher;

/**
 * 权限缓存检查
 *
 * @author Adamancy Zhang at 16:10 on 2024-08-30
 */
@Slf4j
public class AuthCacheChecker {

    private static final String AUTH_CACHE_CHECKER_KEY = "pamirs:check:auth:cache";

    private static final String AUTH_CACHE_CHECKER_VALUE = "1";

    public static void check() {
        StringRedisTemplate stringRedisTemplate = BeanDefinitionUtils.getBean(StringRedisTemplate.class);
        String checkedValue = stringRedisTemplate.opsForValue().get(AUTH_CACHE_CHECKER_KEY);
        if (StringUtils.isBlank(checkedValue) || !AUTH_CACHE_CHECKER_VALUE.equals(checkedValue)) {
            stringRedisTemplate.opsForValue().set(AUTH_CACHE_CHECKER_KEY, AUTH_CACHE_CHECKER_VALUE);
            TimeWatcher.watch(() -> BeanDefinitionUtils.getBean(AuthCacheManager.class).refreshAll(), "auth cache refresh all");
        }
    }
}

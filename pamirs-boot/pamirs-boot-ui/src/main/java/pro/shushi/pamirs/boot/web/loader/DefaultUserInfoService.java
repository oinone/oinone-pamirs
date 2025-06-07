package pro.shushi.pamirs.boot.web.loader;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.web.spi.api.UserAndAuthApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: xuxin
 * @createTime: 2024/05/23 16:46
 */
@Service
@Fun(UserAndAuthApi.FUN_NAMESPACE)
public class DefaultUserInfoService implements UserAndAuthApi {
    @Function
    public Map<String, Object> generatorUserId() {
        Map<String, Object> context = new HashMap<>();
        context.put("currentUser", PamirsSession.getUserId());
        return context;
    }

}
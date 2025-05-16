package pro.shushi.pamirs.auth.api.spi.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.boot.web.spi.api.UserAndAuthApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: xuxin
 * @createTime: 2024/05/23 16:46
 */
@Service
@Fun(UserAndAuthApi.FUN_NAMESPACE)
public class DefaultRoleInfoService implements UserAndAuthApi {

    @Function
    public Map<String, Object> generatorRoleId() {
        Map<String, Object> context = new HashMap<>();
        context.put("currentRole", new ArrayList<>(AuthRoleSession.getCurrentRoles()));
        return context;
    }

}
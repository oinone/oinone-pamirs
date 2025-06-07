package pro.shushi.pamirs.auth.api.runtime.hook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionPrepareApi;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;
import pro.shushi.pamirs.boot.web.spi.holder.UserIdentityHolder;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.List;
import java.util.Set;

/**
 * 角色Hook
 *
 * @author Adamancy Zhang at 17:41 on 2024-01-06
 */
@Slf4j
@Base
@Component
public class RoleHook implements HookBefore {

    private static final String SESSION_PATH_KEY = "path";

    @Autowired
    private ResourcePathParser resourcePathParser;

    @Hook(priority = 5)
    @Override
    public Object run(Function function, Object... args) {
        Set<Long> roleIds = AuthRoleSession.getCurrentRoles();
        if (log.isDebugEnabled()) {
            log.debug("Current user: {} roles: {}", PamirsSession.getUserId(), roleIds);
        }
        prepareAccessInfo(function, args);
        return function;
    }

    private void prepareAccessInfo(Function function, Object... args) {
        parserAccessInfo();
        if (UserIdentityHolder.isAdmin()) {
            return;
        }
        List<AccessPermissionPrepareApi> prepareServices = BeanDefinitionUtils.getBeansOfTypeByOrdered(AccessPermissionPrepareApi.class);
        for (AccessPermissionPrepareApi prepareApi : prepareServices) {
            prepareApi.prepareAccessPermission(function, args);
        }
    }

    private void parserAccessInfo() {
        if (AccessResourceInfoSession.isEnabled()) {
            return;
        }
        String sessionPath = FetchUtil.fetchVariables(SESSION_PATH_KEY);
        if (StringUtils.isBlank(sessionPath)) {
            accessDenied();
            return;
        }
        AccessResourceInfo info = resourcePathParser.parseAccessInfo(sessionPath);
        if (info == null) {
            info = new AccessResourceInfo(sessionPath);
            info.setIsFixed(true);
        }
        AccessResourceInfoSession.setInfo(info);
    }

    private void accessDenied() {
        AccessResourceInfoSession.setInfo(null);
    }
}

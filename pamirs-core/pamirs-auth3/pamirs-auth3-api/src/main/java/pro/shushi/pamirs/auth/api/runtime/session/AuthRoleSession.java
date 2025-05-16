package pro.shushi.pamirs.auth.api.runtime.session;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.SystemRole;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.spi.CurrentRolesCacheApi;
import pro.shushi.pamirs.auth.api.runtime.spi.CustomCurrentRolesFetcher;
import pro.shushi.pamirs.auth.api.service.IRoleCustom;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.session.SessionClearApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * 角色Session
 *
 * @author Adamancy Zhang at 17:24 on 2024-01-06
 */
@Slf4j
@Component
public class AuthRoleSession implements SessionClearApi {

    private static final TransmittableThreadLocal<Set<Long>> storage = new TransmittableThreadLocal<>();

    public static Set<Long> getCurrentRoles() {
        Set<Long> roleIds = storage.get();
        if (roleIds == null) {
            roleIds = loadCurrentRoles();
            PamirsSession.setIsAdmin(roleIds.contains(SystemRole.admin().getId()));
            storage.set(roleIds);
        }
        return roleIds;
    }

    /**
     * 临时切换当前角色（线程不安全）
     *
     * @param temporaryRoleIds 临时角色列表
     */
    public static <T> T usingRoles(Set<Long> temporaryRoleIds, Supplier<T> supplier) {
        Set<Long> roleIds = storage.get();
        Boolean isAdmin = PamirsSession.isAdmin();
        try {
            PamirsSession.setIsAdmin(temporaryRoleIds.contains(SystemRole.admin().getId()));
            storage.set(temporaryRoleIds);
            return supplier.get();
        } finally {
            PamirsSession.setIsAdmin(isAdmin);
            storage.set(roleIds);
        }
    }

    private static Set<Long> loadCurrentRoles() {
        Set<Long> roleIds = loadCustomCurrentRoles();
        if (roleIds != null) {
            return roleIds;
        }
        CurrentRolesCacheApi cacheApi = AuthApiHolder.getCurrentRolesCacheApi();
        roleIds = cacheApi.get();
        if (roleIds == null) {
            roleIds = AuthApiHolder.getCurrentRolesFetcherApi().fetch();
            if (roleIds == null) {
                roleIds = new HashSet<>();
            }
            cacheApi.set(roleIds);
        }
        return roleIds;
    }

    private static Set<Long> loadCustomCurrentRoles() {
        // 旧版本兼容逻辑
        List<IRoleCustom> iRoleCustoms = BeanDefinitionUtils.getBeansOfTypeByOrdered(IRoleCustom.class);
        if (CollectionUtils.isNotEmpty(iRoleCustoms)) {
            List<Long> customRoleIds = iRoleCustoms.get(0).findRoleById(PamirsSession.getUserId());
            if (customRoleIds == null) {
                customRoleIds = Collections.emptyList();
            }
            return new HashSet<>(customRoleIds);
        }
        CustomCurrentRolesFetcher customFetcher = Optional.ofNullable(Spider.getLoader(CustomCurrentRolesFetcher.class).getOrderedExtensions())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.get(0))
                .orElse(null);
        if (customFetcher != null) {
            Set<Long> roleIds = customFetcher.fetch();
            if (roleIds == null) {
                roleIds = new HashSet<>();
            }
            return roleIds;
        }
        return null;
    }

    @Override
    public void clear() {
        storage.remove();
    }
}

package pro.shushi.pamirs.auth.api.service;

import pro.shushi.pamirs.auth.api.runtime.spi.CustomCurrentRolesFetcher;

import java.util.List;

/**
 * @author shier
 * @date 2020/4/22
 * @deprecated please using spi register {@link CustomCurrentRolesFetcher}
 */
@Deprecated
public interface IRoleCustom {

    /**
     * 给定用户ID查询规则
     * @param userId
     * @return
     */
    List<Long> findRoleById(Long userId);

    /**
     * 判断当前用户是否没有任何的权限
     * @return
     */
    @Deprecated
    default Boolean hasNoPermission(List<Long> roleIds) {
        return false;
    }

    /**
     * 用户的角色ID列表是否走缓存，默认走缓存
     * @return
     */
    @Deprecated
    default Boolean userRoleIdsUseCache() {
        return false;
    }

}
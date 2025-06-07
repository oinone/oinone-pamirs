package pro.shushi.pamirs.auth.api.cache.service;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Collection;
import java.util.Set;

/**
 * 用户角色缓存服务
 *
 * @author Adamancy Zhang at 20:12 on 2024-01-06
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthUserRoleCacheService extends StandardSetCacheService<Long, Long> {

    /**
     * 通过用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    Set<Long> get(Long userId);

    /**
     * 设置用户角色缓存
     *
     * @param userId  指定用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    void set(Long userId, Set<Long> roleIds);

    /**
     * 批量设置用户角色缓存
     *
     * @param userIds 指定用户ID集合
     * @param roleIds 角色ID列表集合
     */
    @Override
    void set(Collection<Long> userIds, Collection<Set<Long>> roleIds);

    /**
     * 删除用户角色缓存
     *
     * @param userId 指定用户ID
     * @return 是否删除成功
     */
    @Override
    Boolean delete(Long userId);

    /**
     * 批量删除用户角色缓存
     *
     * @param userIds 指定用户ID列表
     * @return 成功删除数量
     */
    @Override
    Long delete(Set<Long> userIds);

    /**
     * 向缓存中添加指定用户的指定角色列表
     *
     * @param userId  指定用户ID
     * @param roleIds 指定角色ID列表
     * @return 成功追加数量
     */
    @Override
    Long add(Long userId, Set<Long> roleIds);

    /**
     * 向缓存中批量添加指定用户的指定角色列表
     *
     * @param userIds 指定用户ID集合
     * @param roleIds 指定角色ID列表集合
     */
    @Override
    void add(Collection<Long> userIds, Collection<Set<Long>> roleIds);

    /**
     * 从缓存中移除指定用户的指定角色列表
     *
     * @param userId  指定用户ID
     * @param roleIds 指定角色ID列表
     * @return 成功移除数量
     */
    @Override
    Long remove(Long userId, Set<Long> roleIds);

    /**
     * 从缓存中批量移除指定用户的指定角色列表
     *
     * @param userIds 指定用户ID列表
     * @param roleIds 指定角色ID列表集合
     */
    @Override
    void remove(Collection<Long> userIds, Collection<Set<Long>> roleIds);
}

package pro.shushi.pamirs.auth.api.runtime.cache;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 访问权限缓存API
 *
 * @author Adamancy Zhang at 16:35 on 2024-01-04
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AccessPermissionCacheApi {

    /**
     * 获取可访问模块集合
     *
     * @return 可访问模块集合
     */
    AuthResult<Set<String>> fetchAccessModules(Set<Long> roleIds);

    /**
     * 获取可访问首页集合
     *
     * @return 可访问首页集合
     */
    AuthResult<Set<String>> fetchAccessHomepages(Set<Long> roleIds);

    /**
     * 获取可访问菜单集合
     *
     * @return 可访问菜单集合
     */
    AuthResult<Set<String>> fetchAccessMenus(Set<Long> roleIds, String module);

    /**
     * 获取可访问菜单集合
     *
     * @return 可访问菜单集合
     */
    AuthResult<Map<String, Set<String>>> fetchAccessMenus(Set<Long> roleIds, Set<String> modules);

    /**
     * 获取可访问动作集合 - 基于指定跳转动作
     *
     * @return 可访问动作集合
     */
    AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, String model, String actionName);

    /**
     * 获取可访问动作集合 - 基于指定跳转动作
     *
     * @return 可访问动作集合
     */
    AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, Collection<String> models, Collection<String> actionNames);

    /**
     * 获取可访问动作集合 - 基于模型
     *
     * @return 可访问动作集合
     */
    AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, String model);

    /**
     * 获取可访问动作集合 - 基于模型
     *
     * @return 可访问动作集合
     */
    AuthResult<Set<String>> fetchAccessActions(Set<Long> roleIds, Set<String> models);
}

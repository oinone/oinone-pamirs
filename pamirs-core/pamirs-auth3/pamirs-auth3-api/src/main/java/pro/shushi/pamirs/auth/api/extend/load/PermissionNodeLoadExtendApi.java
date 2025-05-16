package pro.shushi.pamirs.auth.api.extend.load;

import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Set;

/**
 * 权限节点扩展API
 *
 * @author Adamancy Zhang at 09:21 on 2024-02-28
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface PermissionNodeLoadExtendApi {

    /**
     * 构造权限加载上下文（仅用于当前扩展）
     *
     * @param defaultLoadContext 默认加载上下文
     * @return 新的权限加载上下文
     */
    default PermissionLoadContext generatorLoadContext(PermissionLoadContext defaultLoadContext) {
        return null;
    }

    /**
     * 加载全部权限项扩展
     *
     * @param loadContext 加载上下文
     * @param nodes       已加载节点集合
     * @param roleIds     角色ID集合
     * @return 新加入的节点集合
     */
    default List<PermissionNode> buildAllPermissions(PermissionLoadContext loadContext, List<PermissionNode> nodes, Set<Long> roleIds) {
        return buildRootPermissions(loadContext, nodes);
    }

    /**
     * 加载根权限项扩展
     *
     * @param loadContext 加载上下文
     * @param nodes       已加载节点集合
     * @return 新加入的节点集合
     */
    default List<PermissionNode> buildRootPermissions(PermissionLoadContext loadContext, List<PermissionNode> nodes) {
        buildRootPermissions(nodes);
        return null;
    }

    /**
     * 加载下级权限项扩展
     *
     * @param selected 当前选中节点
     * @param nodes    已加载节点集合
     * @return 新加入的节点集合
     */
    default List<PermissionNode> buildNextPermissions(PermissionNode selected, List<PermissionNode> nodes) {
        return null;
    }

    /**
     * @deprecated please replace the current method with the buildRootPermissions method for configuring RBAC permissions.
     */
    @Deprecated
    default void buildRootPermissions(List<PermissionNode> nodes) {
    }
}

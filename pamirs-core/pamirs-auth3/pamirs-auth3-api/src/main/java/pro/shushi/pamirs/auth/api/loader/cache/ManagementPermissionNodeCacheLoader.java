package pro.shushi.pamirs.auth.api.loader.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.loader.ActionPermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.entity.AllotMetadataCollection;
import pro.shushi.pamirs.auth.api.loader.entity.PermissionLoadContext;
import pro.shushi.pamirs.auth.api.loader.impl.DefaultManagementPermissionNodeLoader;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * 管理权限节点缓存加载器
 *
 * @author Adamancy Zhang at 15:00 on 2024-09-13
 */
@Slf4j
@Component(AuthConstants.MANAGEMENT_CACHE_LOADER_BEAN_NAME)
public class ManagementPermissionNodeCacheLoader extends DefaultManagementPermissionNodeLoader {

    @Autowired
    private ActionPermissionNodeLoader actionPermissionNodeLoader;

    @Override
    protected List<PermissionNode> buildMenuActionNodesWithCache(String sign, MenuPermissionNode selected, Menu menu) {
        PermissionNodeCache.put(sign, () -> actionPermissionNodeLoader.buildActionNodes(selected, menu));
        return null;
    }

    @Override
    protected void fillCanAccessByAction(PermissionLoadContext loadContext, String module, String menuName, List<PermissionNode> nodes) {
        // do nothing.
    }

    @Override
    protected List<PermissionNode> buildNextPermissionsWithCache(PermissionNode node) {
        if (!ResourcePermissionSubtypeEnum.VIEW_ACTION.equals(node.getNodeType())) {
            return null;
        }
        PermissionNodeCache.put(node.getPath(), () -> buildNextPermissions(node));
        return null;
    }

    @Override
    protected PermissionLoadContext generatorLoadContext(AllotMetadataCollection allotMetadataCollection) {
        return PermissionLoadContext.generatorContext(authAccessService, allotMetadataCollection);
    }

    @Override
    protected PermissionLoadContext generatorLoadContext(AllotMetadataCollection allotMetadataCollection, Set<Long> roleIds) {
        return PermissionLoadContext.generatorContext(authAccessService, allotMetadataCollection);
    }
}

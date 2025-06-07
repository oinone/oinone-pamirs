package pro.shushi.pamirs.auth.view.manager;

import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;

import java.util.List;

/**
 * 权限路径映射操作
 *
 * @author Adamancy Zhang at 16:24 on 2024-03-25
 */
public interface AuthPathMappingOperator {

    void collectionPathMapping(List<PermissionNode> nodes);
}

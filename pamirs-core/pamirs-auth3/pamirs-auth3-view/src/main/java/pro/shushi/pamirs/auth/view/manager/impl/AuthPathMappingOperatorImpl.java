package pro.shushi.pamirs.auth.view.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.auth.api.service.manager.AuthPathMappingManager;
import pro.shushi.pamirs.auth.view.manager.AuthPathMappingOperator;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限路径映射操作
 *
 * @author Adamancy Zhang at 16:25 on 2024-03-25
 */
@Service
public class AuthPathMappingOperatorImpl implements AuthPathMappingOperator {

    @Autowired
    private AuthPathMappingManager authPathMappingManager;

    @Override
    public void collectionPathMapping(List<PermissionNode> nodes) {
        List<AuthPathMapping> pathMappings = new ArrayList<>(16);
        for (PermissionNode node : nodes) {
            String path = node.getPath();
            if (StringUtils.isBlank(path) || !path.endsWith(AuthConstants.ALL_FLAG_STRING)) {
                continue;
            }
            AuthPathMapping pathMapping = new AuthPathMapping();
            pathMapping.setPermissionNodeId(node.getId());
            pathMapping.setPermissionNodeType(node.getNodeType());
            pathMapping.setOriginPath(path);
            pathMapping.setSource(AuthorizationSourceEnum.SYSTEM);
            pathMapping.setNode(node);
            pathMapping.setNodeJson(JsonUtils.toJSONString(node));
            pathMappings.add(pathMapping);
        }
        if (pathMappings.isEmpty()) {
            return;
        }
        authPathMappingManager.collectionPathMappings(pathMappings);
    }
}

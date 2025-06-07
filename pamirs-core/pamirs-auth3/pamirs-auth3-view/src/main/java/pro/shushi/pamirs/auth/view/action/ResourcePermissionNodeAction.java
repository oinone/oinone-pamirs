package pro.shushi.pamirs.auth.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.view.manager.AuthPathMappingOperator;
import pro.shushi.pamirs.auth.view.tmodel.ResourcePermissionNode;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源权限节点动作
 *
 * @author Adamancy Zhang at 18:02 on 2024-01-13
 */
@Component
@Model.model(ResourcePermissionNode.MODEL_MODEL)
public class ResourcePermissionNodeAction {

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Autowired
    private AuthPathMappingOperator authPathMappingOperator;

    @Function(openLevel = FunctionOpenEnum.API, summary = "查询资源权限节点")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ResourcePermissionNode fetchNode(ResourcePermissionNode data) {
        if (data == null) {
            data = new ResourcePermissionNode();
        }
        ResourcePermissionSubtypeEnum nodeType = data.getNodeType();
        if (nodeType == null) {
            return buildRootPermissionTree(data);
        }
        return buildNextPermissionTree(data);
    }

    @Action(displayName = "收集权限项", bindingType = ViewTypeEnum.TABLE)
    public ResourcePermissionNode collectionPathMappings(ResourcePermissionNode data) {
        List<PermissionNode> list = new ArrayList<>(2);
        list.add(ResourcePermissionNode.toNode(data));
        if ("/ui_designer/*".equals(data.getPath())) {
            UeModule printDesignerModule = Models.origin().queryOneByWrapper(Pops.<UeModule>lambdaQuery()
                    .from(UeModule.MODEL_MODEL)
                    .eq(UeModule::getModule, "print_designer"));
            if (printDesignerModule != null) {
                Long resourceId = printDesignerModule.getId();
                ResourcePermissionNode printDesignerModuleNode = new ResourcePermissionNode();
                printDesignerModuleNode.setPath("/print_designer/*");
                printDesignerModuleNode.setNodeType(ResourcePermissionSubtypeEnum.MODULE);
                printDesignerModuleNode.setId(resourceId.toString());
                printDesignerModuleNode.setResourceId(resourceId);
                list.add(ResourcePermissionNode.toNode(printDesignerModuleNode));
            }
        }
        authPathMappingOperator.collectionPathMapping(list);
        return data;
    }

    private ResourcePermissionNode buildRootPermissionTree(ResourcePermissionNode data) {
        ResourcePermissionNodeLoader loader = permissionNodeLoader.getManagementLoader();
        List<PermissionNode> nodes = loader.buildRootPermissions();
        if (CollectionUtils.isNotEmpty(nodes)) {
            data.setNodesJson(JsonUtils.toJSONString(nodes));
        }
        return data;
    }

    private ResourcePermissionNode buildNextPermissionTree(ResourcePermissionNode data) {
        ResourcePermissionNodeLoader loader = permissionNodeLoader.getManagementLoader();
        List<PermissionNode> nodes = loader.buildNextPermissions(ResourcePermissionNode.toNode(data));
        if (CollectionUtils.isNotEmpty(nodes)) {
            data.setNodesJson(JsonUtils.toJSONString(nodes));
        }
        return data;
    }
}

package pro.shushi.pamirs.auth.view.tmodel;

import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 资源权限节点
 *
 * @author Adamancy Zhang at 15:38 on 2024-01-10
 */
@Base
@Model.model(ResourcePermissionNode.MODEL_MODEL)
@Model(displayName = "资源权限节点")
public class ResourcePermissionNode extends TransientModel {

    private static final long serialVersionUID = 688931084474253410L;

    public static final String MODEL_MODEL = "auth.ResourcePermissionNode";

    @Field.String
    @Field(displayName = "JSON格式数据")
    private String nodesJson;

    @Field(displayName = "节点ID")
    private String id;

    @Field(displayName = "父节点ID")
    private String parentId;

    @Field(displayName = "权限组ID")
    private Long groupId;

    @Field(displayName = "是否有子节点")
    private Boolean hasNext;

    @Field(displayName = "节点类型")
    private ResourcePermissionSubtypeEnum nodeType;

    @Field(displayName = "资源ID")
    private Long resourceId;

    @Field(displayName = "资源路径")
    private String path;

    @Field(displayName = "展示值")
    private String displayValue;

    public static ResourcePermissionNode fromNode(PermissionNode data) {
        ResourcePermissionNode node = new ResourcePermissionNode();
        node.setId(data.getId());
        node.setParentId(data.getParentId());
        node.setGroupId(data.getGroupId());
        node.setHasNext(data.getHasNext());
        node.setNodeType(data.getNodeType());
        node.setResourceId(data.getResourceId());
        node.setPath(data.getPath());
        node.setDisplayValue(data.getDisplayValue());
        return node;
    }

    public static PermissionNode toNode(ResourcePermissionNode data) {
        PermissionNode node = new PermissionNode();
        node.setId(data.getId());
        node.setParentId(data.getParentId());
        node.setGroupId(data.getGroupId());
        node.setHasNext(data.getHasNext());
        node.setNodeType(data.getNodeType());
        node.setResourceId(data.getResourceId());
        node.setPath(data.getPath());
        node.setDisplayValue(data.getDisplayValue());
        return node;
    }
}

package pro.shushi.pamirs.auth.api.entity.node;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 权限节点
 *
 * @author Adamancy Zhang at 09:36 on 2024-01-15
 */
public class PermissionNode implements Serializable {

    private static final long serialVersionUID = -605402423496704435L;

    private String id;

    private String parentId;

    @JSONField(serialize = false)
    private PermissionNode parent;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long groupId;

    private Boolean hasNext;

    private Boolean canAccess;

    private Boolean canManagement;

    private Boolean canAllot;

    private ResourcePermissionSubtypeEnum nodeType;

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long resourceId;

    private String resourceCode;

    private String path;

    private String displayValue;

    private List<PermissionNode> nodes;

    @JSONField(serialize = false)
    private Map<String, Object> extend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public PermissionNode getParent() {
        return parent;
    }

    public void setParent(PermissionNode parent) {
        this.parent = parent;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(Boolean canAccess) {
        this.canAccess = canAccess;
    }

    public Boolean getCanManagement() {
        return canManagement;
    }

    public void setCanManagement(Boolean canManagement) {
        this.canManagement = canManagement;
    }

    public Boolean getCanAllot() {
        return canAllot;
    }

    public void setCanAllot(Boolean canAllot) {
        this.canAllot = canAllot;
    }

    public ResourcePermissionSubtypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(ResourcePermissionSubtypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public List<PermissionNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<PermissionNode> nodes) {
        this.nodes = nodes;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }
}

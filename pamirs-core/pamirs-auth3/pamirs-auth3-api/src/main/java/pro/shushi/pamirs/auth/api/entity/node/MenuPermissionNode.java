package pro.shushi.pamirs.auth.api.entity.node;

/**
 * 菜单权限节点
 *
 * @author Adamancy Zhang at 14:18 on 2024-01-15
 */
public class MenuPermissionNode extends PermissionNode {

    private static final long serialVersionUID = -4205132492943149924L;

    private String module;

    private String name;

    private String parentName;

    private Long priority;

    private String model;

    private String action;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

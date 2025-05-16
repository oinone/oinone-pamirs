package pro.shushi.pamirs.auth.api.entity.node;

/**
 * 首页权限节点
 *
 * @author Adamancy Zhang at 14:19 on 2024-01-15
 */
public class HomepagePermissionNode extends PermissionNode {

    private static final long serialVersionUID = 8125789951534680607L;

    private String module;

    private String model;

    private String action;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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

package pro.shushi.pamirs.auth.api.entity.node;

/**
 * 模块权限节点
 *
 * @author Adamancy Zhang at 14:16 on 2024-01-15
 */
public class ModulePermissionNode extends PermissionNode {

    private static final long serialVersionUID = 5787639009015548000L;

    private String module;

    private String icon;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

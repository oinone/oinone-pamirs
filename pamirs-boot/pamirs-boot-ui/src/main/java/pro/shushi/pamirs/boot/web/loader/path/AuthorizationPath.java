package pro.shushi.pamirs.boot.web.loader.path;

import java.util.HashSet;
import java.util.Set;

/**
 * 认证路径
 *
 * @author Adamancy Zhang at 16:31 on 2024-02-22
 */
public class AuthorizationPath {

    private Set<String> modulePaths;

    private Set<String> homepagePaths;

    private Set<String> menuPaths;

    private Set<String> actionPaths;

    public AuthorizationPath() {
        this.modulePaths = new HashSet<>();
        this.homepagePaths = new HashSet<>();
        this.menuPaths = new HashSet<>();
        this.actionPaths = new HashSet<>();
    }

    public Set<String> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(Set<String> modulePaths) {
        this.modulePaths = modulePaths;
    }

    public Set<String> getHomepagePaths() {
        return homepagePaths;
    }

    public void setHomepagePaths(Set<String> homepagePaths) {
        this.homepagePaths = homepagePaths;
    }

    public Set<String> getMenuPaths() {
        return menuPaths;
    }

    public void setMenuPaths(Set<String> menuPaths) {
        this.menuPaths = menuPaths;
    }

    public Set<String> getActionPaths() {
        return actionPaths;
    }

    public void setActionPaths(Set<String> actionPaths) {
        this.actionPaths = actionPaths;
    }
}

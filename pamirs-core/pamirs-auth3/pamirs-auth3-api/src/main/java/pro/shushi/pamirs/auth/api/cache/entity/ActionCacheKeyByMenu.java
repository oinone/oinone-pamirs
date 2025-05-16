package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 动作权限缓存Key - 基于菜单
 *
 * @author Adamancy Zhang at 21:52 on 2024-01-10
 */
public final class ActionCacheKeyByMenu {

    private final Long roleId;

    private final String module;

    private final String menu;

    public ActionCacheKeyByMenu(Long roleId, String module, String menu) {
        this.roleId = roleId;
        this.module = module;
        this.menu = menu;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModule() {
        return module;
    }

    public String getMenu() {
        return menu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCacheKeyByMenu)) return false;
        ActionCacheKeyByMenu that = (ActionCacheKeyByMenu) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(module, that.module) &&
                Objects.equals(menu, that.menu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, module, menu);
    }
}

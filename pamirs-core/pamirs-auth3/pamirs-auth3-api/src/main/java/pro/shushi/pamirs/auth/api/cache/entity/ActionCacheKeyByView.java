package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 动作权限缓存Key - 基于菜单
 *
 * @author Adamancy Zhang at 21:52 on 2024-01-10
 */
public final class ActionCacheKeyByView {

    private final Long roleId;

    private final String module;

    private final String view;

    public ActionCacheKeyByView(Long roleId, String module, String view) {
        this.roleId = roleId;
        this.module = module;
        this.view = view;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModule() {
        return module;
    }

    public String getView() {
        return view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCacheKeyByView)) return false;
        ActionCacheKeyByView that = (ActionCacheKeyByView) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(module, that.module) &&
                Objects.equals(view, that.view);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, module, view);
    }
}

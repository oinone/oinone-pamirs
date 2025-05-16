package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 动作权限缓存Key - 基于跳转动作
 *
 * @author Adamancy Zhang at 21:52 on 2024-01-10
 */
public final class ActionCacheKeyByViewAction {

    private final Long roleId;

    private final String model;

    private final String actionName;

    public ActionCacheKeyByViewAction(Long roleId, String model, String actionName) {
        this.roleId = roleId;
        this.model = model;
        this.actionName = actionName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModel() {
        return model;
    }

    public String getActionName() {
        return actionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCacheKeyByViewAction)) return false;
        ActionCacheKeyByViewAction that = (ActionCacheKeyByViewAction) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(model, that.model) &&
                Objects.equals(actionName, that.actionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, model, actionName);
    }
}

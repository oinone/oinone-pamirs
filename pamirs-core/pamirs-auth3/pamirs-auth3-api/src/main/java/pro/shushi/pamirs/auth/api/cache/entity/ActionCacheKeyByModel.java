package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 动作权限缓存Key - 基于模型
 *
 * @author Adamancy Zhang at 22:02 on 2024-01-10
 */
public final class ActionCacheKeyByModel {

    private final Long roleId;

    private final String model;

    public ActionCacheKeyByModel(Long roleId, String model) {
        this.roleId = roleId;
        this.model = model;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModel() {
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCacheKeyByModel)) return false;
        ActionCacheKeyByModel that = (ActionCacheKeyByModel) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, model);
    }
}

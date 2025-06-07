package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 模型权限缓存Key
 *
 * @author Adamancy Zhang at 14:33 on 2024-01-22
 */
public final class ModelCacheKey {

    private final Long roleId;

    private final String model;

    public ModelCacheKey(Long roleId, String model) {
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
        if (!(o instanceof ModelCacheKey)) return false;
        ModelCacheKey that = (ModelCacheKey) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, model);
    }
}

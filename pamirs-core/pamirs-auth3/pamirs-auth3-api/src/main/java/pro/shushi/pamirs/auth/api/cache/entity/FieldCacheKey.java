package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 字段权限缓存Key
 *
 * @author Adamancy Zhang at 14:12 on 2024-01-20
 */
public final class FieldCacheKey {

    private final Long roleId;

    private final String model;

    public FieldCacheKey(Long roleId, String model) {
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
        if (!(o instanceof FieldCacheKey)) return false;
        FieldCacheKey that = (FieldCacheKey) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, model);
    }
}

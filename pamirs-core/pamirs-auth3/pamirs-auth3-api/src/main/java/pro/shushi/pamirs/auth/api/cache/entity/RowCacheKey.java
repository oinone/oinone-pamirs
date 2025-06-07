package pro.shushi.pamirs.auth.api.cache.entity;

import pro.shushi.pamirs.auth.api.enumeration.authorized.RowAuthorizedValueEnum;

import java.util.Objects;

/**
 * 行权限缓存Key
 *
 * @author Adamancy Zhang at 14:15 on 2024-01-20
 */
public final class RowCacheKey {

    private final Long roleId;

    private final String model;

    private final RowAuthorizedValueEnum authorizedType;

    public RowCacheKey(Long roleId, String model, RowAuthorizedValueEnum authorizedType) {
        this.roleId = roleId;
        this.model = model;
        this.authorizedType = authorizedType;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModel() {
        return model;
    }

    public RowAuthorizedValueEnum getType() {
        return authorizedType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RowCacheKey)) return false;
        RowCacheKey that = (RowCacheKey) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(model, that.model) &&
                authorizedType == that.authorizedType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, model, authorizedType);
    }
}

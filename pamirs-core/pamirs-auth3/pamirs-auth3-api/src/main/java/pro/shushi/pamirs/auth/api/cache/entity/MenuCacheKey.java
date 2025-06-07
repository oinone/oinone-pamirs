package pro.shushi.pamirs.auth.api.cache.entity;

import java.util.Objects;

/**
 * 菜单权限缓存Key
 *
 * @author Adamancy Zhang at 18:48 on 2024-01-10
 */
public final class MenuCacheKey {

    private final Long roleId;

    private final String module;

    public MenuCacheKey(Long roleId, String module) {
        this.roleId = roleId;
        this.module = module;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getModule() {
        return module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuCacheKey)) return false;
        MenuCacheKey that = (MenuCacheKey) o;
        return Objects.equals(roleId, that.roleId) &&
                Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, module);
    }
}

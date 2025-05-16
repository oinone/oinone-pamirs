package pro.shushi.pamirs.bizauth.api.cache.entity;

import java.util.Objects;

public final class BusinessCodeCacheKey {

    private final Long userId;

    private final String BusinessCode;

    public BusinessCodeCacheKey(Long userId, String BusinessCode) {
        this.userId = userId;
        this.BusinessCode = BusinessCode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBusinessCode() {
        return BusinessCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessCodeCacheKey that = (BusinessCodeCacheKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(BusinessCode, that.BusinessCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, BusinessCode);
    }
}

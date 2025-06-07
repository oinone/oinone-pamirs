package pro.shushi.pamirs.auth.view.helper;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.helper.fetch.AuthResourceFetchMethod;
import pro.shushi.pamirs.auth.api.model.AuthGroup;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;

import java.util.List;
import java.util.Set;

/**
 * 获取资源方法
 *
 * @author Adamancy Zhang at 19:44 on 2024-03-02
 */
public abstract class AuthGroupResourceFetchMethod<T extends MetaBaseModel> {

    private final AuthResourceFetchMethod<T> origin;

    protected AuthGroupResourceFetchMethod(AuthResourceFetchMethod<T> origin) {
        this.origin = origin;
    }

    public ResourcePermissionSubtypeEnum getNodeType() {
        return origin.getNodeType();
    }

    public void addResourceId(Long id) {
        origin.addResourceId(id);
    }

    public boolean isEmpty() {
        return origin.isEmpty();
    }

    public List<T> query(Set<Long> resourceIds) {
        return origin.query(resourceIds);
    }

    public AuthResourceAuthorization generatorResourceAuthorization(T data, String path, Long authorizedValue) {
        return origin.generatorResourceAuthorization(data, path, authorizedValue);
    }

    public final MemoryListSearchCache<Long, T> query() {
        return origin.query();
    }

    public abstract AuthGroup createAuthGroup(AuthResourceAuthorization authorization, AuthGroupTypeEnum type);

}

package pro.shushi.pamirs.auth.api.helper.fetch;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.path.ResourcePathParser;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取资源方法
 *
 * @author Adamancy Zhang at 20:14 on 2024-09-10
 */
@Slf4j
public abstract class AuthResourceFetchMethod<T extends MetaBaseModel> {

    private final ResourcePermissionSubtypeEnum nodeType;

    private final Set<Long> resourceIds = new HashSet<>(8);

    protected final AuthAccessService authAccessService;

    protected final ResourcePathParser resourcePathParser;

    protected AuthResourceFetchMethod(ResourcePermissionSubtypeEnum nodeType, AuthAccessService authAccessService) {
        this.nodeType = nodeType;
        this.authAccessService = authAccessService;
        this.resourcePathParser = BeanDefinitionUtils.getBean(ResourcePathParser.class);
    }

    public ResourcePermissionSubtypeEnum getNodeType() {
        return nodeType;
    }

    public void addResourceId(Long id) {
        resourceIds.add(id);
    }

    public boolean isEmpty() {
        return resourceIds.isEmpty();
    }

    public abstract List<T> query(Set<Long> resourceIds);

    public abstract boolean isManagement(T data, String path);

    public abstract boolean isManagement(AuthResourcePermission resourcePermission);

    public AuthResourceAuthorization generatorResourceAuthorization(T data, String path, Long authorizedValue) {
        if (!isManagement(data, path)) {
            return null;
        }
        return rawGeneratorResourceAuthorization(data, path, authorizedValue);
    }

    public abstract AuthResourceAuthorization rawGeneratorResourceAuthorization(T data, String path, Long authorizedValue);

    public final MemoryListSearchCache<Long, T> query() {
        List<T> result;
        if (isEmpty()) {
            result = Collections.emptyList();
        } else {
            result = query(resourceIds);
            if (result == null) {
                result = Collections.emptyList();
            }
        }
        return new MemoryListSearchCache<>(result, IdModel::getId);
    }

}
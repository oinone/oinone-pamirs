package pro.shushi.pamirs.auth.api.service.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthPermission;
import pro.shushi.pamirs.auth.api.behavior.AuthPermissionStandardService;
import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizeModel;
import pro.shushi.pamirs.auth.api.behavior.PermissionAuthorizedValue;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthModelAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthResourceAuthorization;
import pro.shushi.pamirs.auth.api.pmodel.AuthRowAuthorization;
import pro.shushi.pamirs.auth.api.service.authorize.*;
import pro.shushi.pamirs.auth.api.service.manager.AuthAuthorizationOperator;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthModelPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 资源权限项差量服务实现
 *
 * @author Adamancy Zhang at 11:51 on 2024-01-20
 */
@Service
public class AuthAuthorizationOperatorImpl implements AuthAuthorizationOperator {

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthResourceAuthorizeService authResourceAuthorizeService;

    @Autowired
    private AuthModelPermissionService authModelPermissionService;

    @Autowired
    private AuthModelAuthorizeService authModelAuthorizeService;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthFieldAuthorizeService authFieldAuthorizeService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

    @Autowired
    private AuthRowAuthorizeService authRowAuthorizeService;

    @Override
    public List<AuthResourceAuthorization> fillResourcePermissionIds(List<AuthResourceAuthorization> resourceAuthorizations) {
        return new OperatorTemplate<>(
                authResourcePermissionService,
                authResourceAuthorizeService,
                AuthResourcePermission.MODEL_MODEL,
                (authorization) -> AuthResourcePermission.transfer(authorization, new AuthResourcePermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).create(resourceAuthorizations);
    }

    @Override
    public AuthResourceAuthorization createAndAuthorizeResourcePermission(Long roleId, AuthResourceAuthorization resourceAuthorizations) {
        List<AuthResourceAuthorization> results = createAndAuthorizeResourcePermissions(Collections.singleton(roleId), Collections.singletonList(resourceAuthorizations));
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public List<AuthResourceAuthorization> createAndAuthorizeResourcePermissions(Long roleId, List<AuthResourceAuthorization> resourceAuthorizations) {
        return createAndAuthorizeResourcePermissions(Collections.singleton(roleId), resourceAuthorizations);
    }

    @Override
    public List<AuthResourceAuthorization> createAndAuthorizeResourcePermissions(Set<Long> roleIds, List<AuthResourceAuthorization> resourceAuthorizations) {
        return new OperatorTemplate<>(
                authResourcePermissionService,
                authResourceAuthorizeService,
                AuthResourcePermission.MODEL_MODEL,
                (authorization) -> AuthResourcePermission.transfer(authorization, new AuthResourcePermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).createAndAuthorize(roleIds, resourceAuthorizations);
    }

    @Override
    public List<AuthModelAuthorization> fillModelPermissionIds(List<AuthModelAuthorization> modelAuthorizations) {
        return new OperatorTemplate<>(
                authModelPermissionService,
                authModelAuthorizeService,
                AuthModelPermission.MODEL_MODEL,
                (authorization) -> AuthModelPermission.transfer(authorization, new AuthModelPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).create(modelAuthorizations);
    }

    @Override
    public AuthModelAuthorization createAndAuthorizeModelPermission(Long roleId, AuthModelAuthorization modelAuthorization) {
        List<AuthModelAuthorization> results = createAndAuthorizeModelPermissions(Collections.singleton(roleId), Collections.singletonList(modelAuthorization));
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public List<AuthModelAuthorization> createAndAuthorizeModelPermissions(Long roleId, List<AuthModelAuthorization> modelAuthorizations) {
        return createAndAuthorizeModelPermissions(Collections.singleton(roleId), modelAuthorizations);
    }

    @Override
    public List<AuthModelAuthorization> createAndAuthorizeModelPermissions(Set<Long> roleIds, List<AuthModelAuthorization> modelAuthorizations) {
        return new OperatorTemplate<>(
                authModelPermissionService,
                authModelAuthorizeService,
                AuthModelPermission.MODEL_MODEL,
                (authorization) -> AuthModelPermission.transfer(authorization, new AuthModelPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).createAndAuthorize(roleIds, modelAuthorizations);
    }

    @Override
    public List<AuthFieldAuthorization> fillFieldPermissionIds(List<AuthFieldAuthorization> fieldAuthorizations) {
        return new OperatorTemplate<>(
                authFieldPermissionService,
                authFieldAuthorizeService,
                AuthFieldPermission.MODEL_MODEL,
                (authorization) -> AuthFieldPermission.transfer(authorization, new AuthFieldPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).create(fieldAuthorizations);
    }

    @Override
    public AuthFieldAuthorization createAndAuthorizeFieldPermission(Long roleId, AuthFieldAuthorization fieldAuthorization) {
        List<AuthFieldAuthorization> results = createAndAuthorizeFieldPermissions(Collections.singleton(roleId), Collections.singletonList(fieldAuthorization));
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public List<AuthFieldAuthorization> createAndAuthorizeFieldPermissions(Long roleId, List<AuthFieldAuthorization> fieldAuthorizations) {
        return createAndAuthorizeFieldPermissions(Collections.singleton(roleId), fieldAuthorizations);
    }

    @Override
    public List<AuthFieldAuthorization> createAndAuthorizeFieldPermissions(Set<Long> roleIds, List<AuthFieldAuthorization> fieldAuthorizations) {
        return new OperatorTemplate<>(
                authFieldPermissionService,
                authFieldAuthorizeService,
                AuthFieldPermission.MODEL_MODEL,
                (authorization) -> AuthFieldPermission.transfer(authorization, new AuthFieldPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).createAndAuthorize(roleIds, fieldAuthorizations);
    }

    @Override
    public List<AuthRowAuthorization> fillRowPermissionIds(List<AuthRowAuthorization> rowAuthorizations) {
        return new OperatorTemplate<>(
                authRowPermissionService,
                authRowAuthorizeService,
                AuthRowPermission.MODEL_MODEL,
                (authorization) -> AuthRowPermission.transfer(authorization, new AuthRowPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).create(rowAuthorizations);
    }

    @Override
    public AuthRowAuthorization createAndAuthorizeRowPermission(Long roleId, AuthRowAuthorization rowAuthorization) {
        List<AuthRowAuthorization> results = createAndAuthorizeRowPermissions(Collections.singleton(roleId), Collections.singletonList(rowAuthorization));
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public List<AuthRowAuthorization> createAndAuthorizeRowPermissions(Long roleId, List<AuthRowAuthorization> rowAuthorizations) {
        return createAndAuthorizeRowPermissions(Collections.singleton(roleId), rowAuthorizations);
    }

    @Override
    public List<AuthRowAuthorization> createAndAuthorizeRowPermissions(Set<Long> roleIds, List<AuthRowAuthorization> rowAuthorizations) {
        return new OperatorTemplate<>(
                authRowPermissionService,
                authRowAuthorizeService,
                AuthRowPermission.MODEL_MODEL,
                (authorization) -> AuthRowPermission.transfer(authorization, new AuthRowPermission()).setSource(AuthorizationSourceEnum.SYSTEM)
        ).createAndAuthorize(roleIds, rowAuthorizations);
    }

    private static class OperatorTemplate<
            E extends Enum<E> & PermissionAuthorizedValue,
            P extends AuthPermission,
            PA extends PermissionAuthorizeModel<E>
            > {

        private final AuthPermissionStandardService<P> authPermissionStandardService;

        private final AuthPermissionAuthorizeService<E, PA> authPermissionAuthorizeService;

        private final String permissionModel;

        private final Function<PA, P> permissionSupplier;

        public OperatorTemplate(AuthPermissionStandardService<P> authPermissionStandardService,
                                AuthPermissionAuthorizeService<E, PA> authPermissionAuthorizeService,
                                String permissionModel,
                                Function<PA, P> permissionSupplier) {
            this.authPermissionStandardService = authPermissionStandardService;
            this.authPermissionAuthorizeService = authPermissionAuthorizeService;
            this.permissionModel = permissionModel;
            this.permissionSupplier = permissionSupplier;
        }

        public List<PA> create(List<PA> authorizations) {
            Set<String> codes = authorizations.stream().map(PA::getCode).collect(Collectors.toSet());
            List<P> existPermissions = fetchExistPermissions(codes);
            MemoryListSearchCache<String, P> existPermissionCache = new MemoryListSearchCache<>(existPermissions, AuthPermission::sign);
            List<P> createResourcePermissions = new ArrayList<>(authorizations.size());
            Map<String, PA> fillIdAuthorizations = new HashMap<>(authorizations.size());
            for (PA authorization : authorizations) {
                String sign = authorization.sign();
                P existPermission = existPermissionCache.get(sign);
                if (existPermission == null) {
                    createResourcePermissions.add(permissionSupplier.apply(authorization));
                    fillIdAuthorizations.put(sign, authorization);
                } else {
                    ((IdModel) authorization).setId(existPermission.getId());
                }
            }
            if (!createResourcePermissions.isEmpty()) {
                createResourcePermissions = authPermissionStandardService.createBatch(createResourcePermissions);
                for (P createResourcePermission : createResourcePermissions) {
                    ((IdModel) fillIdAuthorizations.get(createResourcePermission.sign())).setId(createResourcePermission.getId());
                }
            }
            return authorizations;
        }

        public List<PA> createAndAuthorize(Set<Long> roleIds, List<PA> authorizations) {
            return authPermissionAuthorizeService.authorizes(roleIds, create(authorizations), AuthorizationSourceEnum.MANUAL);
        }

        private List<P> fetchExistPermissions(Set<String> codes) {
            return DataShardingHelper.build().collectionSharding(codes, (sublist) -> authPermissionStandardService.queryListByWrapper(WrapperHelper.lambda(Pops.<P>query()
                    .from(permissionModel)
                    .setBatchSize(-1)
                    .in(LambdaUtil.fetchFieldName(AuthResourcePermission::getCode), sublist))));
        }
    }
}

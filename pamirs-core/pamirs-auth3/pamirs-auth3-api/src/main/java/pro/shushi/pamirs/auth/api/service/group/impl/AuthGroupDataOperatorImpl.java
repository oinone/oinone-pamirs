package pro.shushi.pamirs.auth.api.service.group.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.behavior.AuthorizedValueComputer;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionTypeEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.permission.AuthFieldPermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.model.permission.AuthRowPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupFieldPermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupResourcePermission;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRole;
import pro.shushi.pamirs.auth.api.model.relation.AuthGroupRowPermission;
import pro.shushi.pamirs.auth.api.service.AuthRoleService;
import pro.shushi.pamirs.auth.api.service.group.AuthGroupDataOperator;
import pro.shushi.pamirs.auth.api.service.permission.AuthFieldPermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.service.permission.AuthRowPermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限组数据操作服务实现
 *
 * @author Adamancy Zhang at 10:46 on 2024-01-19
 */
@Service
public class AuthGroupDataOperatorImpl implements AuthGroupDataOperator {

    @Autowired
    private AuthRoleService authRoleService;

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    @Autowired
    private AuthFieldPermissionService authFieldPermissionService;

    @Autowired
    private AuthRowPermissionService authRowPermissionService;

//    @Override
//    public List<AuthGroupResourcePermission> fetchAllResourcePermissions(Long groupId) {
//        return Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
//                .from(AuthGroupResourcePermission.MODEL_MODEL)
//                .setBatchSize(-1)
//                .eq(AuthGroupResourcePermission::getGroupId, groupId));
//    }
//
//    @Override
//    public List<AuthGroupResourcePermission> fetchResourcePermissions(Long groupId) {
//        return Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
//                .from(AuthGroupResourcePermission.MODEL_MODEL)
//                .setBatchSize(-1)
//                .ne(AuthGroupResourcePermission::getPermissionType, ResourcePermissionTypeEnum.ACTION)
//                .eq(AuthGroupResourcePermission::getGroupId, groupId));
//    }

    @Override
    public List<AuthRole> fetchRoles(Long groupId) {
        Set<Long> roleIds = fetchRoleIds(groupId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return authRoleService.fetchRoles(roleIds);
    }

    @Override
    public Set<Long> fetchRoleIds(Long groupId) {
        List<AuthGroupRole> authGroupRoleRels = Models.origin().queryListByWrapper(Pops.<AuthGroupRole>lambdaQuery()
                .from(AuthGroupRole.MODEL_MODEL)
                .select(AuthGroupRole::getRoleId)
                .eq(AuthGroupRole::getGroupId, groupId));
        if (CollectionUtils.isEmpty(authGroupRoleRels)) {
            return Collections.emptySet();
        }
        return authGroupRoleRels.stream().map(AuthGroupRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public List<AuthGroupResourcePermission> fetchActionPermissions(Long groupId) {
        return Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .eq(AuthGroupResourcePermission::getPermissionType, ResourcePermissionTypeEnum.ACTION.name())
                .eq(AuthGroupResourcePermission::getGroupId, groupId));
    }

    @Override
    public List<AuthGroupResourcePermission> fetchValidActionPermissions(Set<Long> groupIds) {
        return DataShardingHelper.build(100).collectionSharding(groupIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                .from(AuthGroupResourcePermission.MODEL_MODEL)
                .in(AuthGroupResourcePermission::getGroupId, sublist)
                .eq(AuthGroupResourcePermission::getPermissionType, ResourcePermissionTypeEnum.ACTION.name())
                .ge(AuthGroupResourcePermission::getAuthorizedValue, 1)));
    }

    @Override
    public List<AuthGroupResourcePermission> createActionPermissions(List<AuthGroupResourcePermission> data) {
        return Models.origin().createBatch(data);
    }

    @Override
    public Integer updateActionPermissions(List<AuthGroupResourcePermission> data) {
        return Models.origin().updateBatch(data);
    }

    @Override
    public Integer deleteActionPermissionsByGroupId(Long groupId, List<AuthGroupResourcePermission> data) {
        Holder<Integer> count = new Holder<>(0);
        DataShardingHelper.build().sharding(data, (sublist) -> {
            count.set(count.get() + Models.origin().deleteByWrapper(Pops.<AuthGroupResourcePermission>lambdaQuery()
                    .from(AuthGroupResourcePermission.MODEL_MODEL)
                    .eq(AuthGroupResourcePermission::getGroupId, groupId)
                    .in(AuthGroupResourcePermission::getPermissionId, sublist.stream().map(AuthGroupResourcePermission::getPermissionId).collect(Collectors.toList()))));
            return null;
        });
        return count.get();
    }

    @Override
    public List<AuthGroupFieldPermission> fetchFieldPermissions(Long groupId) {
        return Models.origin().queryListByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .select(AuthGroupFieldPermission::getGroupId, AuthGroupFieldPermission::getPermissionId, AuthGroupFieldPermission::getAuthorizedValue)
                .eq(AuthGroupFieldPermission::getGroupId, groupId));
    }

    @Override
    public List<AuthGroupFieldPermission> fetchValidFieldPermissions(Set<Long> groupIds) {
        List<AuthGroupFieldPermission> originFieldPermissions = DataShardingHelper.build(100).collectionSharding(groupIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                .from(AuthGroupFieldPermission.MODEL_MODEL)
                .in(AuthGroupFieldPermission::getGroupId, sublist)
                .ge(AuthGroupFieldPermission::getAuthorizedValue, 1)));
        Map<Long, AuthGroupFieldPermission> collectionPermissions = new HashMap<>(64);
        for (AuthGroupFieldPermission fieldPermission : originFieldPermissions) {
            collectionPermissions.compute(fieldPermission.getPermissionId(), (k, v) -> {
                if (v == null) {
                    return AuthGroupFieldPermission.transfer(fieldPermission, new AuthGroupFieldPermission());
                }
                v.setAuthorizedValue(AuthorizedValueComputer.AUTHORIZE.compute(v.getAuthorizedValue(), fieldPermission.getAuthorizedValue()));
                return v;
            });
        }
        return new ArrayList<>(collectionPermissions.values());
    }

    @Override
    public List<AuthGroupFieldPermission> createFieldPermissions(List<AuthGroupFieldPermission> data) {
        return Models.origin().createBatch(data);
    }

    @Override
    public Integer updateFieldPermissions(List<AuthGroupFieldPermission> data) {
        return Models.origin().updateBatch(data);
    }

    @Override
    public Integer deleteFieldPermissionsByGroupId(Long groupId, List<AuthGroupFieldPermission> data) {
        Holder<Integer> count = new Holder<>(0);
        DataShardingHelper.build().sharding(data, (sublist) -> {
            count.set(count.get() + Models.origin().deleteByWrapper(Pops.<AuthGroupFieldPermission>lambdaQuery()
                    .from(AuthGroupFieldPermission.MODEL_MODEL)
                    .eq(AuthGroupFieldPermission::getGroupId, groupId)
                    .in(AuthGroupFieldPermission::getPermissionId, sublist.stream().map(AuthGroupFieldPermission::getPermissionId).collect(Collectors.toList()))));
            return null;
        });
        return count.get();
    }

    @Override
    public List<AuthGroupRowPermission> fetchRowPermissions(Long groupId) {
        return Models.origin().queryListByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                .from(AuthGroupRowPermission.MODEL_MODEL)
                .eq(AuthGroupRowPermission::getGroupId, groupId));
    }

    @Override
    public List<AuthGroupRowPermission> fetchValidRowPermissions(Set<Long> groupIds) {
        return DataShardingHelper.build().collectionSharding(groupIds, (sublist) -> Models.origin().queryListByWrapper(Pops.<AuthGroupRowPermission>lambdaQuery()
                .from(AuthGroupRowPermission.MODEL_MODEL)
                .in(AuthGroupRowPermission::getGroupId, sublist)
                .ge(AuthGroupRowPermission::getAuthorizedValue, 1)));
    }

    @Override
    public void fillPermissions(List<AuthGroupResourcePermission> actionPermissions, List<AuthGroupFieldPermission> fieldPermissions, List<AuthGroupRowPermission> rowPermissions) {
        if (CollectionUtils.isNotEmpty(actionPermissions)) {
            fillPermissions0(actionPermissions,
                    AuthGroupResourcePermission::getPermissionId, AuthGroupResourcePermission::setPermission,
                    AuthResourcePermission::getId,
                    (ids) -> authResourcePermissionService.queryListByWrapper(Pops.<AuthResourcePermission>lambdaQuery()
                            .from(AuthResourcePermission.MODEL_MODEL)
                            .setBatchSize(-1)
                            .in(AuthResourcePermission::getId, ids)));
        }
        if (CollectionUtils.isNotEmpty(fieldPermissions)) {
            fillPermissions0(fieldPermissions,
                    AuthGroupFieldPermission::getPermissionId, AuthGroupFieldPermission::setPermission,
                    AuthFieldPermission::getId,
                    (ids) -> authFieldPermissionService.queryListByWrapper(Pops.<AuthFieldPermission>lambdaQuery()
                            .from(AuthFieldPermission.MODEL_MODEL)
                            .setBatchSize(-1)
                            .in(AuthFieldPermission::getId, ids)));
        }
        if (CollectionUtils.isNotEmpty(rowPermissions)) {
            fillPermissions0(rowPermissions,
                    AuthGroupRowPermission::getPermissionId, AuthGroupRowPermission::setPermission,
                    AuthRowPermission::getId,
                    (ids) -> authRowPermissionService.queryListByWrapper(Pops.<AuthRowPermission>lambdaQuery()
                            .from(AuthRowPermission.MODEL_MODEL)
                            .setBatchSize(-1)
                            .in(AuthRowPermission::getId, ids)));
        }
    }

    private <T, P> void fillPermissions0(List<T> list,
                                         Function<T, Long> permissionIdGetter, BiConsumer<T, P> permissionSetter,
                                         Function<P, Long> idGetter,
                                         Function<List<Long>, List<P>> query) {
        Map<Long, List<T>> fillPermissionMap = new HashMap<>();
        for (T item : list) {
            fillPermissionMap.computeIfAbsent(permissionIdGetter.apply(item), v -> new ArrayList<>()).add(item);
        }
        List<P> permissions = DataShardingHelper.build().collectionSharding(fillPermissionMap.keySet(), query);
        for (P permission : permissions) {
            List<T> target = fillPermissionMap.get(idGetter.apply(permission));
            if (target != null) {
                target.forEach(v -> permissionSetter.accept(v, permission));
            }
        }
    }
}

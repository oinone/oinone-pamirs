package pro.shushi.pamirs.auth.core.service.authorize;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.authorize.AuthUserAuthorizeService;
import pro.shushi.pamirs.auth.api.service.relation.AuthUserRoleService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户权限授权服务实现
 *
 * @author Adamancy Zhang at 11:51 on 2024-01-08
 */
@Service
@Fun(AuthUserAuthorizeService.FUN_NAMESPACE)
public class AuthUserAuthorizeServiceImpl implements AuthUserAuthorizeService {

    @Autowired
    private AuthUserRoleService authUserRoleService;

    @Function
    @Override
    public Set<Long> queryRoleIds(Long userId) {
        assertUserId(userId);
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryListByUserIds(Sets.newHashSet(userId));
        Set<Long> roleIds = new LinkedHashSet<>(userRoleList.size());
        for (AuthUserRoleRel userRole : userRoleList) {
            roleIds.add(userRole.getRoleId());
        }
        roleIds.addAll(queryUserRoleIdsByAllFlag());
        return roleIds;
    }

    @Function
    @Override
    public Set<Long> queryValidRoleIds(Long userId) {
        assertUserId(userId);
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryValidListByUserIds(Sets.newHashSet(userId));
        Set<Long> roleIds = new LinkedHashSet<>(userRoleList.size());
        for (AuthUserRoleRel userRole : userRoleList) {
            roleIds.add(userRole.getRoleId());
        }
        roleIds.addAll(queryUserRoleIdsByAllFlag());
        return roleIds;
    }

    @Function
    @Override
    public Map<Long, Set<Long>> queryRoleIdsBatch(Set<Long> userIds) {
        Map<Long, Set<Long>> roleIdsMap = new LinkedHashMap<>(userIds.size());
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryListByUserIds(userIds);
        for (AuthUserRoleRel userRole : userRoleList) {
            roleIdsMap.computeIfAbsent(userRole.getUserId(), v -> new LinkedHashSet<>()).add(userRole.getRoleId());
        }
        Set<Long> roleIdsByAllFlag = queryUserRoleIdsByAllFlag();
        for (Set<Long> roleSet : roleIdsMap.values()) {
            roleSet.addAll(roleIdsByAllFlag);
        }
        return roleIdsMap;
    }

    @Function
    @Override
    public Map<Long, Set<Long>> queryValidRoleIdsBatch(Set<Long> userIds) {
        Map<Long, Set<Long>> roleIdsMap = new LinkedHashMap<>(userIds.size());
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryValidListByUserIds(userIds);
        for (AuthUserRoleRel userRole : userRoleList) {
            roleIdsMap.computeIfAbsent(userRole.getUserId(), v -> new LinkedHashSet<>()).add(userRole.getRoleId());
        }
        Set<Long> roleIdsByAllFlag = queryUserRoleIdsByAllFlag();
        for (Set<Long> roleSet : roleIdsMap.values()) {
            roleSet.addAll(roleIdsByAllFlag);
        }
        return roleIdsMap;
    }

    @Function
    @Override
    public Set<Long> queryUserIds(Long roleId) {
        assertRoleId(roleId);
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .select(AuthUserRoleRel::getUserId)
                .setBatchSize(-1)
                .eq(AuthUserRoleRel::getRoleId, roleId));
        return userRoleList.stream().map(AuthUserRoleRel::getUserId).collect(Collectors.toSet());
    }

    @Function
    @Override
    public Map<Long, Set<Long>> queryUserIdsBatch(Set<Long> roleIds) {
        Map<Long, Set<Long>> userIdsMap = new LinkedHashMap<>(roleIds.size());
        List<AuthUserRoleRel> userRoleList = authUserRoleService.queryListByRoleIds(roleIds);
        for (AuthUserRoleRel userRole : userRoleList) {
            userIdsMap.computeIfAbsent(userRole.getRoleId(), v -> new LinkedHashSet<>()).add(userRole.getUserId());
        }
        return userIdsMap;
    }

    @Function
    @Override
    public Boolean authorize(Long userId, Long roleId, AuthorizationSourceEnum source) {
        assertUserId(userId);
        assertRoleId(roleId);
        if (authUserRoleService.count(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .eq(AuthUserRoleRel::getUserId, userId)
                .eq(AuthUserRoleRel::getRoleId, roleId)) >= 1L) {
            return Boolean.FALSE;
        }
        authUserRoleService.create(new AuthUserRoleRel()
                .setUserId(userId)
                .setRoleId(roleId)
                .setSource(source));
        return Boolean.TRUE;
    }

    @Function
    @Override
    public Boolean authorizes(Set<Long> userIds, Set<Long> roleIds, AuthorizationSourceEnum source) {
        Map<Long, Set<Long>> roleIdsMap = queryRoleIdsBatch(userIds);
        List<AuthUserRoleRel> authorizeList = new ArrayList<>(userIds.size() * roleIds.size());
        Set<Long> authorizeUserIds = Sets.difference(userIds, roleIdsMap.keySet());
        for (Long authorizeUserId : authorizeUserIds) {
            for (Long authorizeRoleId : roleIds) {
                authorizeList.add(new AuthUserRoleRel()
                        .setUserId(authorizeUserId)
                        .setRoleId(authorizeRoleId)
                        .setSource(source));
            }
        }
        for (Map.Entry<Long, Set<Long>> roleIdsEntry : roleIdsMap.entrySet()) {
            Long authorizeUserId = roleIdsEntry.getKey();
            Set<Long> authorizeRoleIds = Sets.difference(roleIds, roleIdsEntry.getValue());
            for (Long authorizeRoleId : authorizeRoleIds) {
                authorizeList.add(new AuthUserRoleRel()
                        .setUserId(authorizeUserId)
                        .setRoleId(authorizeRoleId)
                        .setSource(source));
            }
        }
        if (authorizeList.isEmpty()) {
            return Boolean.FALSE;
        }
        authUserRoleService.createBatch(authorizeList);
        return Boolean.TRUE;
    }

    @Function
    @Override
    public Boolean revoke(Long userId, Long roleId) {
        assertUserId(userId);
        assertRoleId(roleId);
        Integer effectRow = authUserRoleService.deleteByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .eq(AuthUserRoleRel::getUserId, userId)
                .eq(AuthUserRoleRel::getRoleId, roleId)
                .ne(AuthUserRoleRel::getSource, AuthorizationSourceEnum.BUILD_IN));
        if (effectRow == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean revokes(Set<Long> userIds, Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(roleIds)) {
            return Boolean.FALSE;
        }
        Integer effectRow = authUserRoleService.deleteByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .in(AuthUserRoleRel::getUserId, userIds)
                .in(AuthUserRoleRel::getRoleId, roleIds)
                .ne(AuthUserRoleRel::getSource, AuthorizationSourceEnum.BUILD_IN));
        if (effectRow >= 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Function
    @Override
    public Boolean fullAuthorize(Long userId, Set<Long> roleIds, AuthorizationSourceEnum source) {
        return fullAuthorizes(Collections.singleton(userId), roleIds, source);
    }

    @Function
    @Override
    public Boolean fullAuthorizes(Set<Long> userIds, Set<Long> roleIds, AuthorizationSourceEnum source) {
        Map<Long, Set<Long>> roleIdsMap = queryRoleIdsBatch(userIds);

        List<AuthUserRoleRel> authorizeList = new ArrayList<>(16);
        Set<Long> revokeTargetUserIds = new HashSet<>(16);
        Set<Long> revokeTargetRoleIds = new HashSet<>(16);
        Set<Long> authorizeUserIds = Sets.difference(userIds, roleIdsMap.keySet());
        for (Long authorizeUserId : authorizeUserIds) {
            for (Long authorizeRoleId : roleIds) {
                authorizeList.add(new AuthUserRoleRel()
                        .setUserId(authorizeUserId)
                        .setRoleId(authorizeRoleId)
                        .setSource(source));
            }
        }
        for (Map.Entry<Long, Set<Long>> roleIdsEntry : roleIdsMap.entrySet()) {
            Long userId = roleIdsEntry.getKey();
            Set<Long> authorizeRoleIds = Sets.difference(roleIds, roleIdsEntry.getValue());
            for (Long authorizeRoleId : authorizeRoleIds) {
                authorizeList.add(new AuthUserRoleRel()
                        .setUserId(userId)
                        .setRoleId(authorizeRoleId)
                        .setSource(source));
            }
            Set<Long> revokeRoleIds = Sets.difference(roleIdsEntry.getValue(), roleIds);
            if (!revokeRoleIds.isEmpty()) {
                revokeTargetUserIds.add(userId);
                revokeTargetRoleIds.addAll(revokeRoleIds);
            }
        }

        Boolean isChange = Boolean.FALSE;
        if (!authorizeList.isEmpty()) {
            authUserRoleService.createBatch(authorizeList);
            isChange = Boolean.TRUE;
        }
        if (revokes(revokeTargetUserIds, revokeTargetRoleIds)) {
            isChange = Boolean.TRUE;
        }
        return isChange;
    }

    protected Set<Long> queryUserRoleIdsByAllFlag() {
        return authUserRoleService.queryRolesByAllFlag().stream().map(AuthUserRoleRel::getRoleId).collect(Collectors.toSet());
    }

    protected void assertUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Invalid user id");
        }
    }

    protected void assertRoleId(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("Invalid role id");
        }
    }
}

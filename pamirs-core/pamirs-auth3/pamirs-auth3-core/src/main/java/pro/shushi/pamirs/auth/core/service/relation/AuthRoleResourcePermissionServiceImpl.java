package pro.shushi.pamirs.auth.core.service.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleResourcePermission;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleResourcePermissionService;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;
import java.util.Set;

/**
 * 角色-资源权限 服务实现
 *
 * @author Adamancy Zhang at 14:04 on 2024-01-08
 */
@Service
@Fun(AuthRoleResourcePermissionService.FUN_NAMESPACE)
public class AuthRoleResourcePermissionServiceImpl extends AbstractStandardModelService<AuthRoleResourcePermission> implements AuthRoleResourcePermissionService {

    @Function
    @Override
    public AuthRoleResourcePermission create(AuthRoleResourcePermission data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthRoleResourcePermission> createBatch(List<AuthRoleResourcePermission> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthRoleResourcePermission update(AuthRoleResourcePermission data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRoleResourcePermission data, LambdaUpdateWrapper<AuthRoleResourcePermission> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRoleResourcePermission> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthRoleResourcePermission createOrUpdate(AuthRoleResourcePermission data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthRoleResourcePermission> delete(List<AuthRoleResourcePermission> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthRoleResourcePermission deleteOne(AuthRoleResourcePermission data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthRoleResourcePermission> queryPage(Pagination<AuthRoleResourcePermission> page, LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthRoleResourcePermission queryOne(AuthRoleResourcePermission query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthRoleResourcePermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRoleResourcePermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRoleResourcePermission> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRoleResourcePermission> queryListByRoleIds(Set<Long> roleIds) {
        return DataShardingHelper.build().collectionSharding(roleIds, (sublist) -> queryListByWrapper(Pops.<AuthRoleResourcePermission>lambdaQuery()
                .from(AuthRoleResourcePermission.MODEL_MODEL)
                .setBatchSize(-1)
                .in(AuthRoleResourcePermission::getRoleId, sublist)));
    }

    @Override
    public List<AuthRoleResourcePermission> queryPermissionIdsByAllFlag() {
        return Models.origin().queryListByWrapper(Pops.<AuthRoleResourcePermission>lambdaQuery()
                .from(AuthRoleResourcePermission.MODEL_MODEL)
                .eq(AuthRoleResourcePermission::getRoleId, AuthConstants.ALL_FLAG_LONG));
    }

    @Override
    protected AuthRoleResourcePermission verificationAndSet(AuthRoleResourcePermission data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthRoleResourcePermission::getSource, AuthRoleResourcePermission::setSource, AuthorizationSourceEnum.MANUAL);
        }
        return data;
    }
}

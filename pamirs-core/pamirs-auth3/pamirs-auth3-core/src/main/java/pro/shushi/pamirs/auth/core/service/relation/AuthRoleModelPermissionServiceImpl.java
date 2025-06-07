package pro.shushi.pamirs.auth.core.service.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.relation.AuthRoleModelPermission;
import pro.shushi.pamirs.auth.api.service.relation.AuthRoleModelPermissionService;
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
 * 角色-模型权限 服务实现
 *
 * @author Adamancy Zhang at 14:04 on 2024-01-08
 */
@Service
@Fun(AuthRoleModelPermissionService.FUN_NAMESPACE)
public class AuthRoleModelPermissionServiceImpl extends AbstractStandardModelService<AuthRoleModelPermission> implements AuthRoleModelPermissionService {

    @Function
    @Override
    public AuthRoleModelPermission create(AuthRoleModelPermission data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthRoleModelPermission> createBatch(List<AuthRoleModelPermission> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthRoleModelPermission update(AuthRoleModelPermission data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthRoleModelPermission data, LambdaUpdateWrapper<AuthRoleModelPermission> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthRoleModelPermission> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthRoleModelPermission createOrUpdate(AuthRoleModelPermission data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthRoleModelPermission> delete(List<AuthRoleModelPermission> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthRoleModelPermission deleteOne(AuthRoleModelPermission data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthRoleModelPermission> queryPage(Pagination<AuthRoleModelPermission> page, LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthRoleModelPermission queryOne(AuthRoleModelPermission query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthRoleModelPermission queryOneByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRoleModelPermission> queryListByWrapper(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthRoleModelPermission> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Function
    @Override
    public List<AuthRoleModelPermission> queryListByRoleIds(Set<Long> roleIds) {
        return DataShardingHelper.build().collectionSharding(roleIds, (sublist) -> queryListByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                .from(AuthRoleModelPermission.MODEL_MODEL)
                .setBatchSize(-1)
                .in(AuthRoleModelPermission::getRoleId, sublist)));
    }

    @Override
    public List<AuthRoleModelPermission> queryPermissionIdsByAllFlag() {
        return Models.origin().queryListByWrapper(Pops.<AuthRoleModelPermission>lambdaQuery()
                .from(AuthRoleModelPermission.MODEL_MODEL)
                .eq(AuthRoleModelPermission::getRoleId, AuthConstants.ALL_FLAG_LONG));
    }

    @Override
    protected AuthRoleModelPermission verificationAndSet(AuthRoleModelPermission data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthRoleModelPermission::getSource, AuthRoleModelPermission::setSource, AuthorizationSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, AuthRoleModelPermission::getInherit, AuthRoleModelPermission::setInherit, Boolean.FALSE);
        }
        return data;
    }
}

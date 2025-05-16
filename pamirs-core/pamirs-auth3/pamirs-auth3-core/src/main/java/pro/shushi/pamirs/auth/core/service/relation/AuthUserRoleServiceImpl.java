package pro.shushi.pamirs.auth.core.service.relation;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.auth.api.service.relation.AuthUserRoleService;
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
 * 用户-角色 服务实现
 *
 * @author Adamancy Zhang at 12:40 on 2024-01-08
 */
@Service
@Fun(AuthUserRoleService.FUN_NAMESPACE)
public class AuthUserRoleServiceImpl extends AbstractStandardModelService<AuthUserRoleRel> implements AuthUserRoleService {

    @Function
    @Override
    public AuthUserRoleRel create(AuthUserRoleRel data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthUserRoleRel> createBatch(List<AuthUserRoleRel> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthUserRoleRel update(AuthUserRoleRel data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthUserRoleRel data, LambdaUpdateWrapper<AuthUserRoleRel> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthUserRoleRel> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthUserRoleRel createOrUpdate(AuthUserRoleRel data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthUserRoleRel> delete(List<AuthUserRoleRel> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthUserRoleRel deleteOne(AuthUserRoleRel data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthUserRoleRel> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthUserRoleRel> queryPage(Pagination<AuthUserRoleRel> page, LambdaQueryWrapper<AuthUserRoleRel> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthUserRoleRel queryOne(AuthUserRoleRel query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthUserRoleRel queryOneByWrapper(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryListByWrapper(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryListByUserIds(Set<Long> userIds) {
        return DataShardingHelper.build().collectionSharding(userIds, (sublist) -> queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .setBatchSize(-1)
                .in(AuthUserRoleRel::getUserId, sublist)));
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryValidListByUserIds(Set<Long> userIds) {
        return DataShardingHelper.build().collectionSharding(userIds, (sublist) -> queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .setBatchSize(-1)
                .eq(AuthUserRoleRel::getActive, Boolean.TRUE)
                .in(AuthUserRoleRel::getUserId, sublist)));
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthUserRoleRel> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryListByRoleIds(Set<Long> roleIds) {
        return DataShardingHelper.build().collectionSharding(roleIds, (sublist) -> queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .setBatchSize(-1)
                .in(AuthUserRoleRel::getRoleId, sublist)));
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryValidListByRoleIds(Set<Long> roleIds) {
        return DataShardingHelper.build().collectionSharding(roleIds, (sublist) -> queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .setBatchSize(-1)
                .eq(AuthUserRoleRel::getActive, Boolean.TRUE)
                .in(AuthUserRoleRel::getRoleId, sublist)));
    }

    @Function
    @Override
    public List<AuthUserRoleRel> queryRolesByAllFlag() {
        return Models.origin().queryListByWrapper(Pops.<AuthUserRoleRel>lambdaQuery()
                .from(AuthUserRoleRel.MODEL_MODEL)
                .eq(AuthUserRoleRel::getUserId, AuthConstants.ALL_FLAG_LONG));
    }

    @Override
    protected AuthUserRoleRel verificationAndSet(AuthUserRoleRel data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthUserRoleRel::getSource, AuthUserRoleRel::setSource, AuthorizationSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, AuthUserRoleRel::getActive, AuthUserRoleRel::setActive, Boolean.TRUE);
        }
        return data;
    }
}

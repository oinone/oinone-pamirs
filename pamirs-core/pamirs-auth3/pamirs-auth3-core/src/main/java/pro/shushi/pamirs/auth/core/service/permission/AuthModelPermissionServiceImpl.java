package pro.shushi.pamirs.auth.core.service.permission;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthModelPermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthModelPermissionService;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 模型权限服务实现
 *
 * @author Adamancy Zhang at 11:51 on 2024-01-08
 */
@Service
@Fun(AuthModelPermissionService.FUN_NAMESPACE)
public class AuthModelPermissionServiceImpl extends AbstractStandardModelService<AuthModelPermission> implements AuthModelPermissionService {

    @Function
    @Override
    public AuthModelPermission create(AuthModelPermission data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthModelPermission> createBatch(List<AuthModelPermission> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthModelPermission update(AuthModelPermission data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthModelPermission data, LambdaUpdateWrapper<AuthModelPermission> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthModelPermission> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthModelPermission createOrUpdate(AuthModelPermission data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthModelPermission> delete(List<AuthModelPermission> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthModelPermission deleteOne(AuthModelPermission data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthModelPermission> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthModelPermission> queryPage(Pagination<AuthModelPermission> page, LambdaQueryWrapper<AuthModelPermission> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthModelPermission queryOne(AuthModelPermission query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthModelPermission queryOneByWrapper(LambdaQueryWrapper<AuthModelPermission> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthModelPermission> queryListByWrapper(LambdaQueryWrapper<AuthModelPermission> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthModelPermission> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected AuthModelPermission verificationAndSet(AuthModelPermission data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthModelPermission::getSource, AuthModelPermission::setSource, AuthorizationSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, AuthModelPermission::getActive, AuthModelPermission::setActive, Boolean.TRUE);
        }
        return data;
    }
}

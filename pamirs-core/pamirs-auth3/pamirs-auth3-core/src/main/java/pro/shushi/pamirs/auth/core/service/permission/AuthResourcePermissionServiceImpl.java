package pro.shushi.pamirs.auth.core.service.permission;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 资源权限服务实现
 *
 * @author Adamancy Zhang at 11:51 on 2024-01-08
 */
@Service
@Fun(AuthResourcePermissionService.FUN_NAMESPACE)
public class AuthResourcePermissionServiceImpl extends AbstractStandardModelService<AuthResourcePermission> implements AuthResourcePermissionService {

    @Function
    @Override
    public AuthResourcePermission create(AuthResourcePermission data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthResourcePermission> createBatch(List<AuthResourcePermission> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthResourcePermission update(AuthResourcePermission data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthResourcePermission data, LambdaUpdateWrapper<AuthResourcePermission> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthResourcePermission> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthResourcePermission createOrUpdate(AuthResourcePermission data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthResourcePermission> delete(List<AuthResourcePermission> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthResourcePermission deleteOne(AuthResourcePermission data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthResourcePermission> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthResourcePermission> queryPage(Pagination<AuthResourcePermission> page, LambdaQueryWrapper<AuthResourcePermission> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthResourcePermission queryOne(AuthResourcePermission query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthResourcePermission queryOneByWrapper(LambdaQueryWrapper<AuthResourcePermission> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthResourcePermission> queryListByWrapper(LambdaQueryWrapper<AuthResourcePermission> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthResourcePermission> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected AuthResourcePermission verificationAndSet(AuthResourcePermission data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthResourcePermission::getSource, AuthResourcePermission::setSource, AuthorizationSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, AuthResourcePermission::getActive, AuthResourcePermission::setActive, Boolean.TRUE);
        }
        return data;
    }
}

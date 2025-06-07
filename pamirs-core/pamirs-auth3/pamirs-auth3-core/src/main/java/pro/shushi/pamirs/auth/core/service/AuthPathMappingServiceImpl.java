package pro.shushi.pamirs.auth.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;
import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.auth.api.service.AuthPathMappingService;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 权限路径映射服务实现
 *
 * @author Adamancy Zhang at 16:08 on 2024-03-25
 */
@Service
@Fun(AuthPathMappingService.FUN_NAMESPACE)
public class AuthPathMappingServiceImpl extends AbstractStandardModelService<AuthPathMapping> implements AuthPathMappingService {

    @Function
    @Override
    public AuthPathMapping create(AuthPathMapping data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthPathMapping> createBatch(List<AuthPathMapping> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthPathMapping update(AuthPathMapping data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthPathMapping data, LambdaUpdateWrapper<AuthPathMapping> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthPathMapping> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthPathMapping createOrUpdate(AuthPathMapping data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthPathMapping> delete(List<AuthPathMapping> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthPathMapping deleteOne(AuthPathMapping data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthPathMapping> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthPathMapping> queryPage(Pagination<AuthPathMapping> page, LambdaQueryWrapper<AuthPathMapping> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthPathMapping queryOne(AuthPathMapping query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthPathMapping queryOneByWrapper(LambdaQueryWrapper<AuthPathMapping> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthPathMapping> queryListByWrapper(LambdaQueryWrapper<AuthPathMapping> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthPathMapping> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected AuthPathMapping verificationAndSet(AuthPathMapping data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, AuthPathMapping::getSource, AuthPathMapping::setSource, AuthorizationSourceEnum.MANUAL);
        }
        return data;
    }
}

package pro.shushi.pamirs.user.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.VerificationHelper;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.user.api.enmu.UserSourceEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.service.UserSimpleService;

import java.util.List;

/**
 * 简单用户服务实现
 *
 * @author Adamancy Zhang at 17:29 on 2024-01-12
 */
@Service
@Fun(UserSimpleService.FUN_NAMESPACE)
public class UserSimpleServiceImpl extends AbstractStandardModelService<PamirsUser> implements UserSimpleService {

    @Function
    @Override
    public PamirsUser create(PamirsUser data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<PamirsUser> createBatch(List<PamirsUser> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public PamirsUser update(PamirsUser data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(PamirsUser data, LambdaUpdateWrapper<PamirsUser> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<PamirsUser> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public PamirsUser createOrUpdate(PamirsUser data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<PamirsUser> delete(List<PamirsUser> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public PamirsUser deleteOne(PamirsUser data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<PamirsUser> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<PamirsUser> queryPage(Pagination<PamirsUser> page, LambdaQueryWrapper<PamirsUser> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public PamirsUser queryOne(PamirsUser query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public PamirsUser queryOneByWrapper(LambdaQueryWrapper<PamirsUser> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<PamirsUser> queryListByWrapper(LambdaQueryWrapper<PamirsUser> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<PamirsUser> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    protected PamirsUser verificationAndSet(PamirsUser data, boolean isUpdate) {
        if (!isUpdate) {
            VerificationHelper.setDefaultValue(data, PamirsUser::getSource, PamirsUser::setSource, UserSourceEnum.MANUAL);
            VerificationHelper.setDefaultValue(data, PamirsUser::getActive, PamirsUser::setActive, Boolean.TRUE);
        }
        return data;
    }
}

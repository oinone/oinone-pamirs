package pro.shushi.pamirs.auth.core.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.model.AuthSharedPageRecord;
import pro.shushi.pamirs.auth.api.service.AuthSharedPageRecordService;
import pro.shushi.pamirs.core.common.standard.service.impl.AbstractStandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 分享页面记录服务实现
 *
 * @author Adamancy Zhang at 15:59 on 2024-04-12
 */
@Service
@Fun(AuthSharedPageRecordService.FUN_NAMESPACE)
public class AuthSharedPageRecordServiceImpl extends AbstractStandardModelService<AuthSharedPageRecord> implements AuthSharedPageRecordService {

    @Function
    @Override
    public AuthSharedPageRecord create(AuthSharedPageRecord data) {
        return super.create(data);
    }

    @Function
    @Override
    public List<AuthSharedPageRecord> createBatch(List<AuthSharedPageRecord> list) {
        return super.createBatch(list);
    }

    @Function
    @Override
    public AuthSharedPageRecord update(AuthSharedPageRecord data) {
        return super.update(data);
    }

    @Function
    @Override
    public Integer updateByWrapper(AuthSharedPageRecord data, LambdaUpdateWrapper<AuthSharedPageRecord> wrapper) {
        return super.updateByWrapper(data, wrapper);
    }

    @Function
    @Override
    public Integer updateBatch(List<AuthSharedPageRecord> list) {
        return super.updateBatch(list);
    }

    @Function
    @Override
    public AuthSharedPageRecord createOrUpdate(AuthSharedPageRecord data) {
        return super.createOrUpdate(data);
    }

    @Function
    @Override
    public List<AuthSharedPageRecord> delete(List<AuthSharedPageRecord> list) {
        return super.delete(list);
    }

    @Function
    @Override
    public AuthSharedPageRecord deleteOne(AuthSharedPageRecord data) {
        return super.deleteOne(data);
    }

    @Function
    @Override
    public Integer deleteByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> wrapper) {
        return super.deleteByWrapper(wrapper);
    }

    @Function
    @Override
    public Pagination<AuthSharedPageRecord> queryPage(Pagination<AuthSharedPageRecord> page, LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper) {
        return super.queryPage(page, queryWrapper);
    }

    @Function
    @Override
    public AuthSharedPageRecord queryOne(AuthSharedPageRecord query) {
        return super.queryOne(query);
    }

    @Function
    @Override
    public AuthSharedPageRecord queryOneByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper) {
        return super.queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<AuthSharedPageRecord> queryListByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper) {
        return super.queryListByWrapper(queryWrapper);
    }

    @Function
    @Override
    public Long count(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper) {
        return super.count(queryWrapper);
    }
}

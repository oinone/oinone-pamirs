package pro.shushi.pamirs.auth.api.service;

import pro.shushi.pamirs.auth.api.model.shared.AuthSharedPageRecord;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 分享页面记录服务
 *
 * @author Adamancy Zhang at 15:54 on 2024-04-12
 */
@Fun(AuthSharedPageRecordService.FUN_NAMESPACE)
public interface AuthSharedPageRecordService extends StandardModelService<AuthSharedPageRecord> {

    String FUN_NAMESPACE = "auth.AuthSharedPageRecordService";

    @Function
    @Override
    AuthSharedPageRecord create(AuthSharedPageRecord data);

    @Function
    @Override
    AuthSharedPageRecord update(AuthSharedPageRecord data);

    @Function
    @Override
    Integer updateByWrapper(AuthSharedPageRecord data, LambdaUpdateWrapper<AuthSharedPageRecord> wrapper);

    @Function
    @Override
    AuthSharedPageRecord createOrUpdate(AuthSharedPageRecord data);

    @Function
    @Override
    List<AuthSharedPageRecord> delete(List<AuthSharedPageRecord> list);

    @Function
    @Override
    AuthSharedPageRecord deleteOne(AuthSharedPageRecord data);

    @Function
    @Override
    Integer deleteByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> wrapper);

    @Function
    @Override
    Pagination<AuthSharedPageRecord> queryPage(Pagination<AuthSharedPageRecord> page, LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper);

    @Function
    @Override
    AuthSharedPageRecord queryOne(AuthSharedPageRecord query);

    @Function
    @Override
    AuthSharedPageRecord queryOneByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper);

    @Function
    @Override
    List<AuthSharedPageRecord> queryListByWrapper(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<AuthSharedPageRecord> queryWrapper);
}

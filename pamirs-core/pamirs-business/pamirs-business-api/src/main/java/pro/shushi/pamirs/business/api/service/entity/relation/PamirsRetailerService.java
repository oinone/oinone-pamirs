package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.entity.relation.PamirsRetailer;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsRetailer}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsRetailerService.FUN_NAMESPACE)
public interface PamirsRetailerService extends StandardModelService<PamirsRetailer> {

    String FUN_NAMESPACE = "business.PamirsRetailerService";

    @Function
    @Override
    PamirsRetailer create(PamirsRetailer data);

    @Function
    @Override
    PamirsRetailer update(PamirsRetailer data);

    @Function
    @Override
    List<PamirsRetailer> delete(List<PamirsRetailer> list);

    @Function
    @Override
    PamirsRetailer deleteOne(PamirsRetailer data);

    @Function
    @Override
    Pagination<PamirsRetailer> queryPage(Pagination<PamirsRetailer> page, LambdaQueryWrapper<PamirsRetailer> queryWrapper);

    @Function
    @Override
    PamirsRetailer queryOne(PamirsRetailer query);

    @Function
    @Override
    PamirsRetailer queryOneByWrapper(LambdaQueryWrapper<PamirsRetailer> queryWrapper);

    @Function
    @Override
    List<PamirsRetailer> queryListByWrapper(LambdaQueryWrapper<PamirsRetailer> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsRetailer> queryWrapper);

    @Function
    PamirsRetailer queryById(Long id);
}

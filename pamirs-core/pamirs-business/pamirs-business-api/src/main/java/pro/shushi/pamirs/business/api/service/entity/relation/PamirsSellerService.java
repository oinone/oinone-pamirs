package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.entity.relation.PamirsSeller;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsSeller}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsSellerService.FUN_NAMESPACE)
public interface PamirsSellerService extends StandardModelService<PamirsSeller> {

    String FUN_NAMESPACE = "business.PamirsSellerService";

    @Function
    @Override
    PamirsSeller create(PamirsSeller data);

    @Function
    @Override
    PamirsSeller update(PamirsSeller data);

    @Function
    @Override
    List<PamirsSeller> delete(List<PamirsSeller> list);

    @Function
    @Override
    PamirsSeller deleteOne(PamirsSeller data);

    @Function
    @Override
    Pagination<PamirsSeller> queryPage(Pagination<PamirsSeller> page, LambdaQueryWrapper<PamirsSeller> queryWrapper);

    @Function
    @Override
    PamirsSeller queryOne(PamirsSeller query);

    @Function
    @Override
    PamirsSeller queryOneByWrapper(LambdaQueryWrapper<PamirsSeller> queryWrapper);

    @Function
    @Override
    List<PamirsSeller> queryListByWrapper(LambdaQueryWrapper<PamirsSeller> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsSeller> queryWrapper);

    @Function
    PamirsSeller queryById(Long id);
}

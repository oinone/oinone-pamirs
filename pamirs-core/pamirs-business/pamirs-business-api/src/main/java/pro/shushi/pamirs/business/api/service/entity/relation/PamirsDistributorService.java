package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.entity.relation.PamirsDistributor;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsDistributor}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsDistributorService.FUN_NAMESPACE)
public interface PamirsDistributorService extends StandardModelService<PamirsDistributor> {

    String FUN_NAMESPACE = "business.PamirsDistributorService";

    @Function
    @Override
    PamirsDistributor create(PamirsDistributor data);

    @Function
    @Override
    PamirsDistributor update(PamirsDistributor data);

    @Function
    @Override
    List<PamirsDistributor> delete(List<PamirsDistributor> list);

    @Function
    @Override
    PamirsDistributor deleteOne(PamirsDistributor data);

    @Function
    @Override
    Pagination<PamirsDistributor> queryPage(Pagination<PamirsDistributor> page, LambdaQueryWrapper<PamirsDistributor> queryWrapper);

    @Function
    @Override
    PamirsDistributor queryOne(PamirsDistributor query);

    @Function
    @Override
    PamirsDistributor queryOneByWrapper(LambdaQueryWrapper<PamirsDistributor> queryWrapper);

    @Function
    @Override
    List<PamirsDistributor> queryListByWrapper(LambdaQueryWrapper<PamirsDistributor> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsDistributor> queryWrapper);

    @Function
    PamirsDistributor queryById(Long id);

    @Function
    Boolean deleteByCode(String code);
}

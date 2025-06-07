package pro.shushi.pamirs.business.api.service.entity.relation;

import pro.shushi.pamirs.business.api.entity.relation.PamirsSupplier;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsSupplier}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsSupplierService.FUN_NAMESPACE)
public interface PamirsSupplierService extends StandardModelService<PamirsSupplier> {

    String FUN_NAMESPACE = "business.PamirsSupplierService";

    @Function
    @Override
    PamirsSupplier create(PamirsSupplier data);

    @Function
    @Override
    PamirsSupplier update(PamirsSupplier data);

    @Function
    @Override
    List<PamirsSupplier> delete(List<PamirsSupplier> list);

    @Function
    @Override
    PamirsSupplier deleteOne(PamirsSupplier data);

    @Function
    @Override
    Pagination<PamirsSupplier> queryPage(Pagination<PamirsSupplier> page, LambdaQueryWrapper<PamirsSupplier> queryWrapper);

    @Function
    @Override
    PamirsSupplier queryOne(PamirsSupplier query);

    @Function
    @Override
    PamirsSupplier queryOneByWrapper(LambdaQueryWrapper<PamirsSupplier> queryWrapper);

    @Function
    @Override
    List<PamirsSupplier> queryListByWrapper(LambdaQueryWrapper<PamirsSupplier> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsSupplier> queryWrapper);

    @Function
    PamirsSupplier queryById(Long id);
}

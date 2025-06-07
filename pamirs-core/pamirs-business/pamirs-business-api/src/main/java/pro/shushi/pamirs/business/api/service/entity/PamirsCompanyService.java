package pro.shushi.pamirs.business.api.service.entity;

import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.core.common.standard.service.StandardModelService;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * {@link PamirsCompany}服务
 *
 * @author Adamancy Zhang at 09:41 on 2021-08-31
 */
@Fun(PamirsCompanyService.FUN_NAMESPACE)
public interface PamirsCompanyService extends StandardModelService<PamirsCompany> {

    String FUN_NAMESPACE = "business.PamirsCompanyService";

    @Function
    @Override
    PamirsCompany create(PamirsCompany data);

    @Function
    @Override
    PamirsCompany update(PamirsCompany data);

    @Function
    @Override
    List<PamirsCompany> delete(List<PamirsCompany> list);

    @Function
    @Override
    PamirsCompany deleteOne(PamirsCompany data);

    @Function
    @Override
    Pagination<PamirsCompany> queryPage(Pagination<PamirsCompany> page, LambdaQueryWrapper<PamirsCompany> queryWrapper);

    @Function
    @Override
    PamirsCompany queryOne(PamirsCompany query);

    @Function
    @Override
    PamirsCompany queryOneByWrapper(LambdaQueryWrapper<PamirsCompany> queryWrapper);

    @Function
    @Override
    List<PamirsCompany> queryListByWrapper(LambdaQueryWrapper<PamirsCompany> queryWrapper);

    @Function
    @Override
    Long count(LambdaQueryWrapper<PamirsCompany> queryWrapper);

    @Function
    void deleteList(List<PamirsCompany> list);

    @Function
    void updateById(PamirsCompany data);

    @Function
    PamirsCompany queryById(Long id);
}

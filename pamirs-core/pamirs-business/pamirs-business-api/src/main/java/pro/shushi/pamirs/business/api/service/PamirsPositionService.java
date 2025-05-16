package pro.shushi.pamirs.business.api.service;

import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;


@Fun(PamirsPositionService.FUN_NAMESPACE)
public interface PamirsPositionService  {

    String FUN_NAMESPACE = "business.PamirsPositionService";

    @Function
    PamirsPosition create(PamirsPosition data);

    @Function
    void updateByPk(PamirsPosition data);

    @Function
    void deleteByPks(List<PamirsPosition> list);

    @Function
    void deleteByPk(PamirsPosition data);

    @Function
    Pagination<PamirsPosition> queryPage(Pagination<PamirsPosition> page, IWrapper<PamirsPosition> queryWrapper);

    @Function
    PamirsPosition queryOne(PamirsPosition query);
}

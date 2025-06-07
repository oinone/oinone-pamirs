package pro.shushi.pamirs.business.api.service;

import pro.shushi.pamirs.business.api.model.PamirsOrganization;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;


@Fun(PamirsOrganizationService.FUN_NAMESPACE)
public interface PamirsOrganizationService {

    String FUN_NAMESPACE = "business.PamirsOrganizationService";

    @Function
    PamirsOrganization create(PamirsOrganization data);

    @Function
    PamirsOrganization update(PamirsOrganization data);

    @Function
    void delete(List<PamirsOrganization> list);

    @Function
    void deleteOne(PamirsOrganization data);

    @Function
    Pagination<PamirsOrganization> queryPage(Pagination<PamirsOrganization> page, IWrapper<PamirsOrganization> queryWrapper);

    @Function
    PamirsOrganization queryOne(PamirsOrganization query);
}

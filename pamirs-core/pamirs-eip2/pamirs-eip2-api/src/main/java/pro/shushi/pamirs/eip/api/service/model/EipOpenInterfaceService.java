package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

@Fun(EipOpenInterfaceService.FUN_NAMESPACE)
public interface EipOpenInterfaceService {
    String FUN_NAMESPACE = "pamirs.eip.EipOpenInterfaceService";

    EipOpenInterface create(EipOpenInterface data);

    EipOpenInterface update(EipOpenInterface data);

    Boolean enable(EipOpenInterface data);

    Boolean disable(EipOpenInterface data);

    Pagination<EipOpenInterface> queryPage(Pagination<EipOpenInterface> page, IWrapper<EipOpenInterface> queryWrapper);
}

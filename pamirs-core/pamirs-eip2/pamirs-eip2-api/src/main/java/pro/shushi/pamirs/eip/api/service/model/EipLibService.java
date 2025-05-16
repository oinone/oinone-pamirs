package pro.shushi.pamirs.eip.api.service.model;


import pro.shushi.pamirs.eip.api.model.EipLib;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Fun(EipLibService.FUN_NAMESPACE)
public interface EipLibService {

    String FUN_NAMESPACE = "pamirs.eip.EipLibService";

    @Function
    Integer createOrUpdate(EipLib eipLib);

    @Function
    EipLib create(EipLib eipLib);

    @Function
    EipLib queryById(Long id);

    @Function
    EipLib queryByCode(String code);

    @Function
    EipLib queryOne(EipLib eipLib);

    @Function
    List<EipLib> queryListByWrapper(IWrapper<EipLib> wrapper);

    @Function
    Integer updateById(EipLib eipLib);
}


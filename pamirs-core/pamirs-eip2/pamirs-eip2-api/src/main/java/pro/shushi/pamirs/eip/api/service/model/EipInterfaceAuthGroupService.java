package pro.shushi.pamirs.eip.api.service.model;


import pro.shushi.pamirs.eip.api.model.EipInterfaceAuthGroup;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Fun(EipInterfaceAuthGroupService.FUN_NAMESPACE)
public interface EipInterfaceAuthGroupService {

    String FUN_NAMESPACE = "pamirs.eip.EipInterfaceAuthGroupService";

    @Function
    Integer createOrUpdate(EipInterfaceAuthGroup eipInterfaceAuthGroup);

    @Function
    EipInterfaceAuthGroup create(EipInterfaceAuthGroup eipInterfaceAuthGroup);

    @Function
    EipInterfaceAuthGroup queryById(Long id);

    @Function
    EipInterfaceAuthGroup queryOne(EipInterfaceAuthGroup eipInterfaceAuthGroup);

    @Function
    List<EipInterfaceAuthGroup> queryListByWrapper(IWrapper<EipInterfaceAuthGroup> wrapper);

    @Function
    Integer updateById(EipInterfaceAuthGroup eipInterfaceAuthGroup);
}


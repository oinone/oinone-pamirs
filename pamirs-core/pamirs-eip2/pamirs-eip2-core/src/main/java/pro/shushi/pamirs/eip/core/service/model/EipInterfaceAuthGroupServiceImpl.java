package pro.shushi.pamirs.eip.core.service.model;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipInterfaceAuthGroup;
import pro.shushi.pamirs.eip.api.service.model.EipInterfaceAuthGroupService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Slf4j
@Component
@Fun(EipInterfaceAuthGroupService.FUN_NAMESPACE)
public class EipInterfaceAuthGroupServiceImpl implements EipInterfaceAuthGroupService {


    @Override
    @Function
    public Integer createOrUpdate(EipInterfaceAuthGroup eipInterfaceAuthGroup) {
        return null;
    }

    @Override
    @Function
    public EipInterfaceAuthGroup create(EipInterfaceAuthGroup eipInterfaceAuthGroup) {
        return eipInterfaceAuthGroup.create();
    }

    @Override
    @Function
    public EipInterfaceAuthGroup queryById(Long id) {
        return null;
    }

    @Override
    @Function
    public EipInterfaceAuthGroup queryOne(EipInterfaceAuthGroup eipInterfaceAuthGroup) {
        return null;
    }

    @Override
    @Function
    public List<EipInterfaceAuthGroup> queryListByWrapper(IWrapper<EipInterfaceAuthGroup> wrapper) {
        return null;
    }

    @Override
    @Function
    public Integer updateById(EipInterfaceAuthGroup eipInterfaceAuthGroup) {
        return null;
    }
}

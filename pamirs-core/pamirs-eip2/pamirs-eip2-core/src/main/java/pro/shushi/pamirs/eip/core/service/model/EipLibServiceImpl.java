package pro.shushi.pamirs.eip.core.service.model;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipLib;
import pro.shushi.pamirs.eip.api.service.model.EipLibService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Slf4j
@Component
@Fun(EipLibService.FUN_NAMESPACE)
public class EipLibServiceImpl implements EipLibService {


    @Override
    @Function
    public Integer createOrUpdate(EipLib eipLib) {
        return null;
    }

    @Override
    @Function
    public EipLib create(EipLib eipLib) {
        return null;
    }

    @Override
    @Function
    public EipLib queryById(Long id) {
        return null;
    }

    @Override
    @Function
    public EipLib queryByCode(String code) {
        return new EipLib().queryByCode(code);
    }

    @Override
    @Function
    public EipLib queryOne(EipLib eipLib) {
        return null;
    }

    @Override
    @Function
    public List<EipLib> queryListByWrapper(IWrapper<EipLib> wrapper) {
        return null;
    }

    @Override
    @Function
    public Integer updateById(EipLib eipLib) {
        return null;
    }
}

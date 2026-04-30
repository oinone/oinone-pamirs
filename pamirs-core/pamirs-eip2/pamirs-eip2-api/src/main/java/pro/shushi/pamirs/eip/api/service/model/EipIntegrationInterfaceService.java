package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;

@Fun(EipIntegrationInterfaceService.FUN_NAMESPACE)
public interface EipIntegrationInterfaceService {
    String FUN_NAMESPACE = "pamirs.eip.EipIntegrationInterfaceService";

    @Function
    Integer createOrUpdate(EipIntegrationInterface eipIntegrationInterface);

    @Function
    EipIntegrationInterface create(EipIntegrationInterface eipIntegrationInterface);

    @Function
    EipIntegrationInterface queryById(Long id);

    @Function
    EipIntegrationInterface queryOne(EipIntegrationInterface one);

    @Function
    List<EipIntegrationInterface> queryListByWrapper(IWrapper<EipIntegrationInterface> wrapper);

    @Function
    Integer updateById(EipIntegrationInterface update);

    @Function
    Pagination<EipIntegrationInterface> queryPage(Pagination<EipIntegrationInterface> page, IWrapper<EipIntegrationInterface> queryWrapper);

    @Function
    Pagination<EipIntegrationInterface> loadForAlarmRule(Pagination<EipIntegrationInterface> page, IWrapper<EipIntegrationInterface> queryWrapper);

    @Function
    EipIntegrationInterface queryByInterfaceName(String interfaceName);

    @Function
    List<EipIntegrationInterface> queryByIds(List<Long> ids);
}
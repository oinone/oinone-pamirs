package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;

import java.util.List;
import java.util.Map;

/**
 * @author Nation
 * @cdate 2021-04-02 10:35
 */
@Fun(ResourceCountryService.FUN_NAMESPACE)
public interface ResourceCountryService {

    String FUN_NAMESPACE = "pamirs.resource.ResourceCountryService";

    @Function
    ResourceCountry queryById(Long id);

    @Function
    ResourceCountry queryByCode(String code);

    @Function
    List<ResourceCountry> queryListByCodes(List<String> codes);

    @Function
    Map<String/*code*/, ResourceCountry> queryMapByCodes(List<String> codes);

    @Function
    Map<Long/*id*/, ResourceCountry> queryAllIdsMap();

    @Function
    Map<String/*code*/, ResourceCountry> queryAllCodesMap();

    @Function
    List<ResourceCountry> queryListByWrapper(IWrapper<ResourceCountry> queryWrapper);

    @Function
    ResourceCountry queryByName(String name);
}

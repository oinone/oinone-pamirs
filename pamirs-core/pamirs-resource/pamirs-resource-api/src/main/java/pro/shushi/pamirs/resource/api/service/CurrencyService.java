package pro.shushi.pamirs.resource.api.service;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;

import java.util.List;
import java.util.Map;

/**
 * @author Nation
 * @cdate 2021-04-02 10:35
 */
@Fun(CurrencyService.FUN_NAMESPACE)
public interface CurrencyService {

    String FUN_NAMESPACE = "pamirs.resource.CurrencyService";

    @Function
    ResourceCurrency queryById(Long id);

    @Function
    ResourceCurrency queryByCode(String code);

    @Function
    List<ResourceCurrency> queryListByCodes(List<String> codes);

    @Function
    Map<String/*currencyCode*/, ResourceCurrency> queryMapByCodes(List<String> codes);

    @Function
    Map<Long/*id*/, ResourceCurrency> queryAllIdsMap();

    @Function
    Map<String/*code*/, ResourceCurrency> queryAllCodesMap();
}

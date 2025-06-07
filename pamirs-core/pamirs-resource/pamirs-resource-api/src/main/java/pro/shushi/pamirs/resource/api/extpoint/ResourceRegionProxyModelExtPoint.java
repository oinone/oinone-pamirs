package pro.shushi.pamirs.resource.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;
import pro.shushi.pamirs.resource.api.enmu.AddressTypeEnum;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.tmodel.ResourceRegionProxyModel;

import java.util.*;
import java.util.function.Function;

@Ext(ResourceRegionProxyModel.class)
public class ResourceRegionProxyModelExtPoint extends DefaultReadWriteExtPoint<ResourceRegionProxyModel> {

    private static final Function<String, ResourceCountry> countrySupplierByCode = code -> new ResourceCountry().setCode(code).queryByCode();

    private static final Function<String, ResourceProvince> provinceSupplierByCode = code -> new ResourceProvince().setCode(code).queryByCode();

    private static final Function<String, ResourceCity> citySupplierByCode = code -> new ResourceCity().setCode(code).queryByCode();

    private static final Function<String, ResourceDistrict> districtSupplierByCode = code -> new ResourceDistrict().setCode(code).queryByCode();

    private static final Function<String, ResourceStreet> streetSupplierByCode = code -> new ResourceStreet().setCode(code).queryByCode();

    @Override
    @ExtPoint.Implement
    public Pagination<ResourceRegionProxyModel> queryPageAfter(Pagination<ResourceRegionProxyModel> page) {
        return page.setContent(queryParentRegion(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), page.getContent()));
    }

    private List<ResourceRegionProxyModel> queryParentRegion(Map<String, ResourceCountry> countryCache,
                                                             Map<String, ResourceProvince> provinceCache,
                                                             Map<String, ResourceCity> cityCache,
                                                             Map<String, ResourceDistrict> districtCache,
                                                             Map<String, ResourceStreet> streetCache,
                                                             List<ResourceRegionProxyModel> list) {
        if (CollectionUtils.isEmpty(list))
            return new ArrayList<>();
        for (ResourceRegionProxyModel item : list) {
            String code = item.getCode();
            AddressTypeEnum addressType = item.getType();
            if (addressType == null)
                continue;
            switch (addressType) {
                case Country:
                    item.setCountry(fetchModelCacheByCode(code, countryCache, countrySupplierByCode));
                    break;
                case Province:
                    ResourceProvince province = fetchModelCacheByCode(code, provinceCache, provinceSupplierByCode);
                    item.setProvince(province);
                    Optional.ofNullable(province).map(ResourceProvince::getCountry).map(ResourceCountry::getCode).ifPresent(v -> item.setCountry(fetchModelCacheByCode(v, countryCache, countrySupplierByCode)));
                    break;
                case City:
                    ResourceCity city = fetchModelCacheByCode(code, cityCache, citySupplierByCode);
                    item.setCity(city);
                    Optional.ofNullable(city).map(ResourceCity::getCountry).map(ResourceCountry::getCode).ifPresent(v -> item.setCountry(fetchModelCacheByCode(v, countryCache, countrySupplierByCode)));
                    Optional.ofNullable(city).map(ResourceCity::getProvince).map(ResourceProvince::getCode).ifPresent(v -> item.setProvince(fetchModelCacheByCode(v, provinceCache, provinceSupplierByCode)));
                    break;
                case District:
                    ResourceDistrict district = fetchModelCacheByCode(code, districtCache, districtSupplierByCode);
                    item.setDistrict(district);
                    Optional.ofNullable(district).map(ResourceDistrict::getCountry).map(ResourceCountry::getCode).ifPresent(v -> item.setCountry(fetchModelCacheByCode(v, countryCache, countrySupplierByCode)));
                    Optional.ofNullable(district).map(ResourceDistrict::getProvince).map(ResourceProvince::getCode).ifPresent(v -> item.setProvince(fetchModelCacheByCode(v, provinceCache, provinceSupplierByCode)));
                    Optional.ofNullable(district).map(ResourceDistrict::getCity).map(ResourceCity::getCode).ifPresent(v -> item.setCity(fetchModelCacheByCode(v, cityCache, citySupplierByCode)));
                    break;
                case Street:
                    ResourceStreet street = fetchModelCacheByCode(code, streetCache, streetSupplierByCode);
                    item.setStreet(street);
                    Optional.ofNullable(street).map(ResourceStreet::getCountry).map(ResourceCountry::getCode).ifPresent(v -> item.setCountry(fetchModelCacheByCode(v, countryCache, countrySupplierByCode)));
                    Optional.ofNullable(street).map(ResourceStreet::getProvince).map(ResourceProvince::getCode).ifPresent(v -> item.setProvince(fetchModelCacheByCode(v, provinceCache, provinceSupplierByCode)));
                    Optional.ofNullable(street).map(ResourceStreet::getCity).map(ResourceCity::getCode).ifPresent(v -> item.setCity(fetchModelCacheByCode(v, cityCache, citySupplierByCode)));
                    Optional.ofNullable(street).map(ResourceStreet::getDistrict).map(ResourceDistrict::getCode).ifPresent(v -> item.setDistrict(fetchModelCacheByCode(v, districtCache, districtSupplierByCode)));
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    private <T> T fetchModelCacheByCode(String code, Map<String, T> cache, Function<String, T> supplier) {
        T result = cache.get(code);
        if (result == null) {
            result = supplier.apply(code);
            cache.put(code, result);
        }
        return result;
    }
}

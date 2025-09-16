package pro.shushi.pamirs.resource.api.spi.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.ResourceModelQueryService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.resource.api.model.*;

import java.util.List;

/**
 * @author Gesi at 9:56 on 2025/9/16
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
@SPI.Service
public class ResourceResourceModelQueryService implements ResourceModelQueryService {

    @Override
    public D queryResourceAddressByName(String sourceType, String countryName, String provinceName, String cityName, String districtName, String streetName) {
        ResourceAddress address = new ResourceAddress();

        if (StringUtils.isNotBlank(countryName)) {
            List<ResourceCountry> resourceCountries = new ResourceCountry().queryList(
                    Pops.<ResourceCountry>lambdaQuery().from(ResourceCountry.MODEL_MODEL)
                            .eq(StringUtils.isNotBlank(sourceType), ResourceCountry::getSourceType, sourceType)
                            .eq(ResourceCountry::getName, countryName)
            );
            if (resourceCountries.size() != 1) {
                return address;
            }
            ResourceCountry country = resourceCountries.get(0);
            address.setOriginCountry(country);
            address.setCountryName(countryName);
            address.setCountryCode(country.getCode());
        }
        if (StringUtils.isNotBlank(provinceName)) {
            List<ResourceProvince> resourceProvinces = new ResourceProvince().queryList(
                    Pops.<ResourceProvince>lambdaQuery().from(ResourceProvince.MODEL_MODEL)
                            .eq(StringUtils.isNotBlank(sourceType), ResourceProvince::getSourceType, sourceType)
                            .eq(ResourceProvince::getName, provinceName)
                            .eq(StringUtils.isNotBlank(address.getCountryCode()), ResourceProvince::getCountryCode, address.getCountryCode())
            );
            if (resourceProvinces.size() != 1) {
                return address;
            }
            ResourceProvince province = resourceProvinces.get(0);
            address.setOriginProvince(province);
            address.setProvinceName(province.getName());
            address.setProvinceCode(province.getCode());
        }
        if (StringUtils.isNotBlank(cityName)) {
            List<ResourceCity> resourceCities = new ResourceCity().queryList(
                    Pops.<ResourceCity>lambdaQuery().from(ResourceCity.MODEL_MODEL)
                            .eq(StringUtils.isNotBlank(sourceType), ResourceCity::getSourceType, sourceType)
                            .eq(ResourceCity::getName, cityName)
                            .eq(StringUtils.isNotBlank(address.getProvinceCode()), ResourceCity::getProvinceCode, address.getProvinceCode())
            );
            if (resourceCities.size() != 1) {
                return address;
            }
            ResourceCity city = resourceCities.get(0);
            address.setOriginCity(city);
            address.setCityName(city.getName());
            address.setCityCode(city.getCode());
        }
        if (StringUtils.isNotBlank(districtName)) {
            List<ResourceDistrict> resourceDistricts = new ResourceDistrict().queryList(
                    Pops.<ResourceDistrict>lambdaQuery().from(ResourceDistrict.MODEL_MODEL)
                            .eq(StringUtils.isNotBlank(sourceType), ResourceDistrict::getSourceType, sourceType)
                            .eq(ResourceDistrict::getName, districtName)
                            .eq(StringUtils.isNotBlank(address.getCityCode()), ResourceDistrict::getCityCode, address.getCityCode())
            );
            if (resourceDistricts.size() != 1) {
                return address;
            }
            ResourceDistrict district = resourceDistricts.get(0);
            address.setOriginDistrict(district);
            address.setDistrictName(district.getName());
            address.setDistrictCode(district.getCode());
        }
        if (StringUtils.isNotBlank(streetName)) {
            List<ResourceStreet> resourceStreets = new ResourceStreet().queryList(
                    Pops.<ResourceStreet>lambdaQuery().from(ResourceStreet.MODEL_MODEL)
                            .eq(StringUtils.isNotBlank(sourceType), ResourceStreet::getSourceType, sourceType)
                            .eq(ResourceStreet::getName, streetName)
                            .eq(StringUtils.isNotBlank(address.getDistrictCode()), ResourceStreet::getDistrictCode, address.getDistrictCode())
            );
            if (resourceStreets.size() != 1) {
                return address;
            }
            ResourceStreet street = resourceStreets.get(0);
            address.setOriginStreet(street);
            address.setStreetName(street.getName());
            address.setStreetCode(street.getCode());
        }

        return address;
    }

}

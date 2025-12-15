package pro.shushi.pamirs.resource.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.service.ResourceAddressService;

import java.util.List;

@Fun(ResourceAddressService.FUN_NAMESPACE)
@Component
public class ResourceAddressServiceImpl implements ResourceAddressService {

    private IWrapper<ResourceAddress> initQueryWrapper(IWrapper<ResourceAddress> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceAddress>) queryWrapper).from(ResourceAddress.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceAddress>) queryWrapper).lambda().from(ResourceAddress.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public Pagination<ResourceAddress> queryPage(Pagination<ResourceAddress> page, IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryPage(page, queryWrapper);
    }

    @Override
    @Function
    public List<ResourceAddress> queryListByWrapper(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryList(queryWrapper);
    }

    @Override
    @Function
    public List<ResourceAddress> queryListByEntity(ResourceAddress entity) {
        //entity.set(Long.valueOf(new ResourceMajorConfig().singletonModel().getPartnerId()));
        return entity.queryList();
    }

    @Override
    @Function
    public Long count(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return Models.origin().count(queryWrapper);
    }

    @Override
    @Function
    public ResourceAddress queryOneByWrapper(IWrapper<ResourceAddress> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceAddress().queryOneByWrapper(queryWrapper);
    }

    @Override
    @Function
    public ResourceAddress queryById(Long id) {
        return new ResourceAddress().queryById(id);
    }

    @Override
    @Function
    public List<ResourceAddress> queryByIds(List<Long> ids) {
        return FetchUtil.fetchListByIds(ResourceAddress.MODEL_MODEL, ids);
    }

    @Override
    @Function
    public Integer updateById(ResourceAddress data) {
        return data.updateById();
    }

    @Override
    @Function
    public ResourceAddress create(ResourceAddress data) {
        return data.create();
    }

    @Function
    @Override
    public ResourceAddress fillAddressByName(ResourceAddress data) {
        ResourceAddress address = new ResourceAddress();
        String countryCode = null;
        String countryName = data.getCountryName();
        if (StringUtils.isBlank(countryName)) {
            countryCode = DefaultResourceConstants.COUNTRY_CODE;
            ResourceCountry CN = new ResourceCountry().setCode(DefaultResourceConstants.COUNTRY_CODE).queryByCode();
            if (CN != null) {
                address.setOriginCountry(CN);
                address.setCountryCode(CN.getCode());
                address.setCountryName(CN.getName());
            }
        } else {
            List<ResourceCountry> resourceCountries = Models.origin().queryListByWrapper(
                    new Pagination<>(1, 1),
                    Pops.<ResourceCountry>lambdaQuery().from(ResourceCountry.MODEL_MODEL)
                            .eq(ResourceCountry::getName, countryName)
            );
            if (CollectionUtils.isNotEmpty(resourceCountries)) {
                ResourceCountry country = resourceCountries.get(0);
                countryCode = country.getCode();
                address.setOriginCountry(country);
                address.setCountryName(countryName);
                address.setCountryCode(country.getCode());
            }
        }
        String provinceCode = null;
        String provinceName = data.getProvinceName();
        if (StringUtils.isNotBlank(provinceName)) {
            List<ResourceProvince> resourceProvinces = Models.origin().queryListByWrapper(
                    new Pagination<>(1, 1),
                    Pops.<ResourceProvince>lambdaQuery().from(ResourceProvince.MODEL_MODEL)
                            .eq(ResourceProvince::getName, provinceName)
                            .eq(ResourceProvince::getCountryCode, countryCode)
            );
            if (CollectionUtils.isNotEmpty(resourceProvinces)) {
                ResourceProvince province = resourceProvinces.get(0);
                provinceCode = province.getCode();
                address.setOriginProvince(province);
                address.setProvinceName(province.getName());
                address.setProvinceCode(province.getCode());
            }
        }
        String cityCode = null;
        String cityName = data.getCityName();
        if (StringUtils.isNoneBlank(provinceCode, cityName)) {
            List<ResourceCity> resourceCities = Models.origin().queryListByWrapper(
                    new Pagination<>(1, 1),
                    Pops.<ResourceCity>lambdaQuery().from(ResourceCity.MODEL_MODEL)
                            .eq(ResourceCity::getName, cityName)
                            .eq(ResourceCity::getCountryCode, countryCode)
                            .eq(ResourceCity::getProvinceCode, provinceCode)
            );
            if (CollectionUtils.isNotEmpty(resourceCities)) {
                ResourceCity city = resourceCities.get(0);
                cityCode = city.getCode();
                address.setOriginCity(city);
                address.setCityName(city.getName());
                address.setCityCode(city.getCode());
            }
        }
        String districtCode = null;
        String districtName = data.getDistrictName();
        if (StringUtils.isNoneBlank(cityCode, districtName)) {
            List<ResourceDistrict> resourceDistricts = Models.origin().queryListByWrapper(
                    new Pagination<>(1, 1),
                    Pops.<ResourceDistrict>lambdaQuery().from(ResourceDistrict.MODEL_MODEL)
                            .eq(ResourceDistrict::getName, districtName)
                            .eq(ResourceDistrict::getCountryCode, countryCode)
                            .eq(ResourceDistrict::getProvinceCode, provinceCode)
                            .eq(ResourceDistrict::getCityCode, cityCode)
            );
            if (CollectionUtils.isNotEmpty(resourceDistricts)) {
                ResourceDistrict district = resourceDistricts.get(0);
                districtCode = district.getCode();
                address.setOriginDistrict(district);
                address.setDistrictName(district.getName());
                address.setDistrictCode(district.getCode());
            }
        }
        String streetName = data.getStreetName();
        if (StringUtils.isNoneBlank(districtCode, streetName)) {
            List<ResourceStreet> resourceStreets = new ResourceStreet().queryListByWrapper(
                    new Pagination<>(1, 1),
                    Pops.<ResourceStreet>lambdaQuery().from(ResourceStreet.MODEL_MODEL)
                            .eq(ResourceStreet::getName, streetName)
                            .eq(ResourceStreet::getCountryCode, countryCode)
                            .eq(ResourceStreet::getProvinceCode, provinceCode)
                            .eq(ResourceStreet::getCityCode, cityCode)
                            .eq(ResourceStreet::getDistrictCode, districtCode)
            );
            if (CollectionUtils.isNotEmpty(resourceStreets)) {
                ResourceStreet street = resourceStreets.get(0);
                address.setOriginStreet(street);
                address.setStreetName(street.getName());
                address.setStreetCode(street.getCode());
            }
        }
        return address;
    }
}

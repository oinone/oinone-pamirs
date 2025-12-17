package pro.shushi.pamirs.resource.api.quickfilling;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractNonBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.util.*;

/**
 * 地址转换
 *
 * @author Adamancy Zhang at 11:49 on 2025-11-28
 */
public class ResourceAddressConverter extends AbstractNonBasicQuickFillingConverter implements QuickFillingConverter {

    private static final TypeReference<Map<String, String>> PARAM_VALUE_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    private static final String FIELD_FORMAT = "%s#%s";

    private static final String COUNTRY_FIELD = "originCountry";

    private static final String PROVINCE_FIELD = "originProvince";

    private static final String CITY_FIELD = "originCity";

    private static final String DISTRICT_FIELD = "originDistrict";

    private static final String STREET_FIELD = "originStreet";

    private final String field;

    private final List<Pair<ResourceAddress, QuickFillingRow>> matchValues;

    private final Set<String> countryNames;

    private final Set<String> provinceNames;

    private final Set<String> cityNames;

    private final Set<String> districtNames;

    private final Set<String> streetNames;

    public ResourceAddressConverter(QuickFillingColumn column) {
        super(column);
        this.field = getColumn().getField();
        this.matchValues = new ArrayList<>();
        this.countryNames = new HashSet<>();
        this.provinceNames = new HashSet<>();
        this.cityNames = new HashSet<>();
        this.districtNames = new HashSet<>();
        this.streetNames = new HashSet<>();
    }

    @Override
    public void collect(QuickFillingRow row, String value) {
        QuickFillingColumn column = getColumn();
        boolean required = column.isRequired();
        if (StringUtils.isBlank(value)) {
            if (required) {
                row.validateRequired(getCountryField());
                row.validateRequired(getProvinceField());
                row.validateRequired(getCityField());
                row.validateRequired(getDistrictField());
                row.validateRequired(getStreetField());
            }
            return;
        }
        Map<String, String> addressValue = JsonUtils.parseObject(value, PARAM_VALUE_TYPE_REFERENCE);
        String countryName = addressValue.get(COUNTRY_FIELD);
        String provinceName = addressValue.get(PROVINCE_FIELD);
        String cityName = addressValue.get(CITY_FIELD);
        String districtName = addressValue.get(DISTRICT_FIELD);
        String streetName = addressValue.get(STREET_FIELD);
        ResourceAddress address = new ResourceAddress();
        boolean isValid = false;
        if (StringUtils.isNotBlank(countryName)) {
            address.setCountryName(countryName);
            countryNames.add(countryName);
            isValid = true;
        }
        if (StringUtils.isNotBlank(provinceName)) {
            isValid = true;
            address.setProvinceName(provinceName);
            provinceNames.add(provinceName);
            if (StringUtils.isNotBlank(cityName)) {
                address.setCityName(cityName);
                cityNames.add(cityName);
                if (StringUtils.isNotBlank(districtName)) {
                    address.setDistrictName(districtName);
                    districtNames.add(districtName);
                    if (StringUtils.isNotBlank(streetName)) {
                        address.setStreetName(streetName);
                        streetNames.add(streetName);
                    }
                }
            }
        }
        if (isValid) {
            matchValues.add(Pair.of(address, row));
        } else if (required) {
            if (StringUtils.isBlank(countryName)) {
                row.validateRequired(getCountryField());
            } else {
                row.validateError(getCountryField(), QuickFillingExpEnumerate.COUNTRY_DATA_ERROR.msg());
            }
            if (StringUtils.isBlank(provinceName)) {
                row.validateRequired(getProvinceField());
            } else {
                row.validateError(getProvinceField(), QuickFillingExpEnumerate.PROVINCE_DATA_ERROR.msg());
            }
            if (StringUtils.isBlank(cityName)) {
                row.validateRequired(getCityField());
            } else {
                row.validateError(getCityField(), QuickFillingExpEnumerate.CITY_DATA_ERROR.msg());
            }
            if (StringUtils.isBlank(districtName)) {
                row.validateRequired(getDistrictField());
            } else {
                row.validateError(getDistrictField(), QuickFillingExpEnumerate.DISTRICT_DATA_ERROR.msg());
            }
            if (StringUtils.isBlank(streetName)) {
                row.validateRequired(getStreetField());
            } else {
                row.validateError(getStreetField(), QuickFillingExpEnumerate.STREET_DATA_ERROR.msg());
            }
        }
    }

    @Override
    public void fill() {
        if (matchValues.isEmpty()) {
            return;
        }
        boolean isValid = false;
        ResourceCountry CN = new ResourceCountry().setCode(DefaultResourceConstants.COUNTRY_CODE).queryByCode();
        List<ResourceCountry> countryList;
        if (countryNames.isEmpty()) {
            countryList = new ArrayList<>();
            if (CN != null) {
                countryList.add(CN);
            }
        } else {
            countryList = queryListByNames(ResourceCountry.MODEL_MODEL, ResourceCountry::getName, countryNames);
            if (CN != null && !countryNames.contains(CN.getName())) {
                countryList.add(CN);
            }
            isValid = true;
        }
        List<ResourceProvince> provinceList = queryListByNames(ResourceProvince.MODEL_MODEL, ResourceProvince::getName, provinceNames);
        List<ResourceCity> cityList = null;
        List<ResourceDistrict> districtList = null;
        List<ResourceStreet> streetList = null;
        if (!provinceList.isEmpty()) {
            isValid = true;
            cityList = queryListByNames(ResourceCity.MODEL_MODEL, ResourceCity::getName, cityNames);
            if (!cityList.isEmpty()) {
                districtList = queryListByNames(ResourceDistrict.MODEL_MODEL, ResourceDistrict::getName, districtNames);
                if (!districtList.isEmpty()) {
                    streetList = queryListByNames(ResourceStreet.MODEL_MODEL, ResourceStreet::getName, streetNames);
                }
            }
        }
        if (!isValid) {
            return;
        }
        if (cityList == null) {
            cityList = new ArrayList<>();
        }
        if (districtList == null) {
            districtList = new ArrayList<>();
        }
        if (streetList == null) {
            streetList = new ArrayList<>();
        }
        MemoryListSearchCache<String, ResourceCountry> countryCache = new MemoryListSearchCache<>(countryList, ResourceCountry::getName);
        MemoryListSearchCache<String, ResourceProvince> provinceCache = new MemoryListSearchCache<>(provinceList,
                v -> StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                        v.getCountryCode(),
                        v.getName()));
        MemoryListSearchCache<String, ResourceCity> cityCache = new MemoryListSearchCache<>(cityList,
                v -> StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                        v.getCountryCode(),
                        v.getProvinceCode(),
                        v.getName()));
        MemoryListSearchCache<String, ResourceDistrict> districtCache = new MemoryListSearchCache<>(districtList,
                v -> StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                        v.getCountryCode(),
                        v.getProvinceCode(),
                        v.getCityCode(),
                        v.getName()));
        MemoryListSearchCache<String, ResourceStreet> streetCache = new MemoryListSearchCache<>(streetList,
                v -> StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                        v.getCountryCode(),
                        v.getProvinceCode(),
                        v.getCityCode(),
                        v.getDistrictCode(),
                        v.getName()));
        for (Pair<ResourceAddress, QuickFillingRow> matchValue : matchValues) {
            ResourceAddress resourceAddress = matchValue.getKey();
            if (matchCountry(resourceAddress, CN, countryCache)) {
                boolean ignoredResult = matchProvince(resourceAddress, provinceCache) &&
                        matchCity(resourceAddress, cityCache) &&
                        matchDistrict(resourceAddress, districtCache) &&
                        matchStreet(resourceAddress, streetCache);
            }
            ResourceCountry country = resourceAddress.getOriginCountry();
            if (country != null) {
                matchValue.getValue().setValue(getCountryField(), country);
            } else if (StringUtils.isNotBlank(resourceAddress.getCountryName())) {
                matchValue.getValue().validateError(getCountryField(), QuickFillingExpEnumerate.COUNTRY_DATA_ERROR.msg());
            }
            ResourceProvince province = resourceAddress.getOriginProvince();
            if (province != null) {
                matchValue.getValue().setValue(getProvinceField(), province);
            } else if (StringUtils.isNotBlank(resourceAddress.getProvinceName())) {
                matchValue.getValue().validateError(getProvinceField(), QuickFillingExpEnumerate.PROVINCE_DATA_ERROR.msg());
            }
            ResourceCity city = resourceAddress.getOriginCity();
            if (city != null) {
                matchValue.getValue().setValue(getCityField(), city);
            } else if (StringUtils.isNotBlank(resourceAddress.getCityName())) {
                matchValue.getValue().validateError(getCityField(), QuickFillingExpEnumerate.CITY_DATA_ERROR.msg());
            }
            ResourceDistrict district = resourceAddress.getOriginDistrict();
            if (district != null) {
                matchValue.getValue().setValue(getDistrictField(), district);
            } else if (StringUtils.isNotBlank(resourceAddress.getDistrictName())) {
                matchValue.getValue().validateError(getDistrictField(), QuickFillingExpEnumerate.DISTRICT_DATA_ERROR.msg());
            }
            ResourceStreet street = resourceAddress.getOriginStreet();
            if (street != null) {
                matchValue.getValue().setValue(getStreetField(), street);
            } else if (StringUtils.isNotBlank(resourceAddress.getStreetName())) {
                matchValue.getValue().validateError(getStreetField(), QuickFillingExpEnumerate.STREET_DATA_ERROR.msg());
            }
        }
    }

    private boolean matchCountry(ResourceAddress resourceAddress, ResourceCountry CN, MemoryListSearchCache<String, ResourceCountry> countryCache) {
        String countryName = resourceAddress.getCountryName();
        if (StringUtils.isBlank(countryName)) {
            // FIXME: zbh 20251128 最小化设置
            resourceAddress.setOriginCountry(CN);
            resourceAddress.setCountryCode(CN.getCode());
            resourceAddress.setCountryName(CN.getName());
        } else {
            ResourceCountry country = countryCache.get(countryName);
            if (country == null) {
                return false;
            }
            // FIXME: zbh 20251128 最小化设置
            resourceAddress.setOriginCountry(country);
            resourceAddress.setCountryCode(country.getCode());
            resourceAddress.setCountryName(country.getName());
        }
        return true;
    }

    private boolean matchProvince(ResourceAddress resourceAddress, MemoryListSearchCache<String, ResourceProvince> provinceCache) {
        String provinceName = resourceAddress.getProvinceName();
        if (StringUtils.isBlank(provinceName)) {
            return false;
        }
        ResourceProvince province = provinceCache.get(StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                resourceAddress.getCountryCode(),
                provinceName));
        if (province == null) {
            return false;
        }
        // FIXME: zbh 20251128 最小化设置
        resourceAddress.setOriginProvince(province);
        resourceAddress.setProvinceCode(province.getCode());
        resourceAddress.setProvinceName(province.getName());
        return true;
    }

    private boolean matchCity(ResourceAddress resourceAddress, MemoryListSearchCache<String, ResourceCity> cityCache) {
        String cityName = resourceAddress.getCityName();
        if (StringUtils.isBlank(cityName)) {
            return false;
        }
        ResourceCity city = cityCache.get(StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                resourceAddress.getCountryCode(),
                resourceAddress.getProvinceCode(),
                cityName));
        if (city == null) {
            return false;
        }
        // FIXME: zbh 20251128 最小化设置
        resourceAddress.setOriginCity(city);
        resourceAddress.setCityCode(city.getCode());
        resourceAddress.setCityName(city.getName());
        return true;
    }

    private boolean matchDistrict(ResourceAddress resourceAddress, MemoryListSearchCache<String, ResourceDistrict> districtCache) {
        String districtName = resourceAddress.getDistrictName();
        if (StringUtils.isBlank(districtName)) {
            return false;
        }
        ResourceDistrict district = districtCache.get(StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                resourceAddress.getCountryCode(),
                resourceAddress.getProvinceCode(),
                resourceAddress.getCityCode(),
                districtName));
        if (district == null) {
            return false;
        }
        // FIXME: zbh 20251128 最小化设置
        resourceAddress.setOriginDistrict(district);
        resourceAddress.setDistrictCode(district.getCode());
        resourceAddress.setDistrictName(district.getName());
        return true;
    }

    private boolean matchStreet(ResourceAddress resourceAddress, MemoryListSearchCache<String, ResourceStreet> streetCache) {
        String streetName = resourceAddress.getStreetName();
        if (StringUtils.isBlank(streetName)) {
            return false;
        }
        ResourceStreet street = streetCache.get(StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                resourceAddress.getCountryCode(),
                resourceAddress.getProvinceCode(),
                resourceAddress.getCityCode(),
                resourceAddress.getDistrictCode(),
                streetName));
        if (street == null) {
            return false;
        }
        // FIXME: zbh 20251128 最小化设置
        resourceAddress.setOriginStreet(street);
        resourceAddress.setStreetCode(street.getCode());
        resourceAddress.setStreetName(street.getName());
        return true;
    }

    private <T> List<T> queryListByNames(String model, Getter<T, ?> column, Set<String> names) {
        if (names.isEmpty()) {
            return new ArrayList<>();
        }
        return DataShardingHelper.build().collectionSharding(names,
                (sublist) -> Models.origin().queryListByWrapper(Pops.<T>lambdaQuery()
                        .from(model)
                        .in(column, sublist)
                        .setSortable(false)
                        .setBatchSize(-1)));
    }

    private String getCountryField() {
        return String.format(FIELD_FORMAT, field, COUNTRY_FIELD);
    }

    private String getProvinceField() {
        return String.format(FIELD_FORMAT, field, PROVINCE_FIELD);
    }

    private String getCityField() {
        return String.format(FIELD_FORMAT, field, CITY_FIELD);
    }

    private String getDistrictField() {
        return String.format(FIELD_FORMAT, field, DISTRICT_FIELD);
    }

    private String getStreetField() {
        return String.format(FIELD_FORMAT, field, STREET_FIELD);
    }
}

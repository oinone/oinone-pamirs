package pro.shushi.pamirs.filling.converter;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.filling.enumeration.QuickFillingExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.service.ResourceAddressService;

import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
public class M2OConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new M2OConverter();

    private static final TypeReference<Map<String, String>> PARAM_VALUE_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    @Override
    public Object convert(QuickFillingContext context, String value) {
        String references = context.getModelFieldConfig().getReferences();
        if (ResourceAddress.MODEL_MODEL.equals(references)) {
            return convertResourceAddress(context, value);
        }
        QueryWrapper<Object> wrapper = Pops.query().from(references);
        fillQueryWrapperCondition(context, references, wrapper, value);
        if (context.isFailed()) {
            return null;
        }
        List<Object> relationList = Models.origin().queryListByWrapper(new Pagination<>(1, 2), wrapper);
        if (CollectionUtils.isEmpty(relationList)) {
            context.fail("未查询到对应数据");
            return null;
        } else if (relationList.size() != 1) {
            context.fail("查询到多条数据");
            return null;
        }
        return relationList.get(0);
    }

    @Override
    protected Object singleValueConvert(QuickFillingContext context, String value) {
        // do nothing.
        return null;
    }

    private void fillQueryWrapperCondition(QuickFillingContext context, String model, QueryWrapper<?> queryWrapper, String value) {
        List<String> labelFields = context.getLabelFields();
        if (CollectionUtils.isEmpty(labelFields)) {
            context.fail("未指定选项字段无法进行匹配");
            return;
        }
        // FIXME: zbh 20251112 此处不允许使用 or 拼接，后续需要改成多次匹配查询
        String labelField = labelFields.get(0);
        ModelFieldConfig labelFieldConfig = PamirsSession.getContext().getModelField(model, labelField);
        if (labelFieldConfig == null) {
            throw PamirsException.construct(QuickFillingExpEnumerate.FIELD_NOT_FIND).appendMsg(labelField + "字段在模型" + model + "内找不到").errThrow();
        }
        queryWrapper.eq(labelFieldConfig.getColumn(), value);
    }

    private ResourceAddress convertResourceAddress(QuickFillingContext context, String value) {
        Map<String, String> addressValue = JsonUtils.parseObject(value, PARAM_VALUE_TYPE_REFERENCE);
        String countryName = addressValue.get("originCountry");
        String provinceName = addressValue.get("originProvince");
        String cityName = addressValue.get("originCity");
        String districtName = addressValue.get("originDistrict");
        String streetName = addressValue.get("originStreet");
        ResourceAddressService resourceAddressService = BeanDefinitionUtils.getBean(ResourceAddressService.class);
        if (resourceAddressService == null) {
            return null;
        }
        ResourceAddress address = new ResourceAddress();
        address.setCountryName(countryName);
        address.setProvinceName(provinceName);
        address.setCityName(cityName);
        address.setDistrictName(districtName);
        address.setStreetName(streetName);
        address = resourceAddressService.fillAddressByName(address);
        if (address == null) {
            return null;
        }
        String field = context.getField();
        ResourceCountry originCountry = address.getOriginCountry();
        String countryField = String.format("%s#%s", field, "originCountry");
        if (originCountry == null) {
            if (StringUtils.isNotBlank(countryName)) {
                context.fail(countryField, "未查询到或查询到多条国家数据");
            }
        } else {
            Models.d(context.getTarget()).put(countryField, originCountry);
        }
        ResourceProvince originProvince = address.getOriginProvince();
        String provinceField = String.format("%s#%s", field, "originProvince");
        if (originProvince == null) {
            if (StringUtils.isNotBlank(provinceName)) {
                context.fail(provinceField, "未查询到或查询到多条省/州数据");
            }
        } else {
            Models.d(context.getTarget()).put(provinceField, originProvince);
        }
        ResourceCity originCity = address.getOriginCity();
        String cityField = String.format("%s#%s", field, "originCity");
        if (originCity == null) {
            if (StringUtils.isNotBlank(cityName)) {
                context.fail(cityField, "未查询到或查询到多条城市数据");
            }
        } else {
            Models.d(context.getTarget()).put(cityField, originCity);
        }
        ResourceDistrict originDistrict = address.getOriginDistrict();
        String districtField = String.format("%s#%s", field, "originDistrict");
        if (originDistrict == null) {
            if (StringUtils.isNotBlank(districtName)) {
                context.fail(districtField, "未查询到或查询到多条区/县数据");
            }
        } else {
            Models.d(context.getTarget()).put(districtField, originDistrict);
        }
        ResourceStreet originStreet = address.getOriginStreet();
        String streetField = String.format("%s#%s", field, "originStreet");
        if (originStreet == null) {
            if (StringUtils.isNotBlank(streetName)) {
                context.fail(streetField, "未查询到或查询到多条街道数据");
            }
        } else {
            Models.d(context.getTarget()).put(streetField, originStreet);
        }
        return address;
    }

}

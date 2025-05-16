package pro.shushi.pamirs.resource.api.function;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.resource.api.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ResourceRegionMappingFunction {

//    @Action(displayName = "测试")
//    public ResourceRegionMapping test(ResourceRegionMapping data) {
//        matchRegion(ResourceProvince.class, "浙江");
//        matchRegion(ResourceProvince.class, "浙江省");
//        matchRegion(ResourceProvince.class, "浙江", COUNTRY_CODE);
//        matchRegion(ResourceProvince.class, "浙江省", COUNTRY_CODE);
//
//        return data;
//    }

    /**
     * 根据关键字和地区类型,先匹配对应的表,匹配不到再匹配关键字表
     *
     * @param clazz    #{@link pro.shushi.pamirs.resource.api.model.ResourceCountry, pro.shushi.pamirs.resource.api.model.ResourceCity 等地区模型}
     * @param keywords
     * @param <T>
     * @return
     */
    public <T> T matchRegion(Class<T> clazz, String keywords) throws org.apache.ibatis.exceptions.TooManyResultsException {
        return matchRegion(clazz, keywords, null);
    }

    /**
     * @param parentCode 上级地区编码,解决重名问题
     * @param <T>
     * @return
     */
    public <T> T matchRegion(Class<T> clazz, String keywords, String parentCode) throws org.apache.ibatis.exceptions.TooManyResultsException {
        if (clazz == null || StringUtils.isBlank(keywords)) {
            return null;
        }
        //先通过name直接匹配
        T result = queryRegionByName(clazz, keywords, parentCode);
        if (result != null) {
            if (log.isDebugEnabled()) {
                log.debug("关键字:{} 从:{} 直接匹配到数据", keywords, clazz.getName());
            }
            return result;
        }
        //再通过映射表匹配
        List<ResourceRegionMapping> mappings = mappingRegionCodes(clazz, keywords);
        if (CollectionUtils.isEmpty(mappings)) {
            return null;
        }
        List<String> codes = mappings.stream().map(ResourceRegionMapping::getRelationCode).collect(Collectors.toList());
        if (log.isDebugEnabled()) {
            log.debug("关键字:{} 通过关键字匹配到地区codes:{} ", keywords, JSON.toJSONString(codes));
        }
        //根据匹配到的关键字code,以及指定的parent,确认地区编码
        String parentFieldName = StringUtils.isBlank(parentCode) ? null : _getParentFieldName(clazz);
        return Models.data().queryOneByWrapper(
                Pops.<T>query().from(clazz)
                        .in("code", codes)
                        .eq(!StringUtils.isBlank(parentFieldName), parentFieldName, parentCode)
        );
    }

    private <T> T queryRegionByName(Class<T> clazz, String name, String parentCode) {
        if (clazz == null || StringUtils.isBlank(name)) {
            return null;
        }
        String parentFieldName = StringUtils.isBlank(parentCode) ? null : _getParentFieldName(clazz);
        List<T> list = Models.data().queryListByWrapper(
                Pops.<T>query().from(clazz)
                        .eq("name", name)
                        .eq(StringUtils.isNotBlank(parentFieldName), parentFieldName, parentCode)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过关键字匹配地区编码列表
     *
     * @param clazz
     * @param keywords
     * @param <T>
     * @return
     */
    private <T> List<ResourceRegionMapping> mappingRegionCodes(Class<T> clazz, String keywords) {
        if (clazz == null || StringUtils.isBlank(keywords)) {
            return null;
        }
        String model = Models.api().getModel(clazz);
        if (StringUtils.isBlank(model)) {
            return null;
        }
        List<ResourceRegionMapping> mappings = Models.data().queryListByWrapper(
                Pops.<ResourceRegionMapping>lambdaQuery().from(ResourceRegionMapping.MODEL_MODEL).eq(ResourceRegionMapping::getModel, model).eq(ResourceRegionMapping::getKeywords, keywords)
        );
        return mappings;
    }

    private String _getParentFieldName(Class<?> clazz) {
        if (clazz.equals(ResourceCountry.class)) {
            return null;
        } else if (clazz.equals(ResourceProvince.class)) {
            return "country_code";
        } else if (clazz.equals(ResourceCity.class)) {
            return "province_code";
        } else if (clazz.equals(ResourceDistrict.class)) {
            return "city_code";
        } else if (clazz.equals(ResourceStreet.class)) {
            return "district_code";
        }
        log.error("地区关键字匹配,传入的地区级别class异常:{}", clazz.getName());
        return null;
    }
}

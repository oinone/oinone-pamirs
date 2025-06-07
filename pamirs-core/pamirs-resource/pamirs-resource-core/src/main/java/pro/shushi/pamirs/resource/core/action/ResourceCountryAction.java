package pro.shushi.pamirs.resource.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;

import java.util.List;

@Component
@Model.model(ResourceCountry.MODEL_MODEL)
public class ResourceCountryAction {

    /**
     * 配合前端组件
     *
     * @param data
     * @return
     */
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.REMOTE}, summary = "获取到国家一级的数据")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<ResourceCountry> fetchAllData(ResourceCountry data) {
        try (BatchSizeHintApi batchSize = BatchSizeHintApi.use(-1)) {
            return new ResourceCountry().queryList();
        }
    }

    @Function.Advanced(displayName = "登录页查询国家手机号", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<ResourceCountry> queryPhoneCodes() {
        List<ResourceCountry> countryList = Models.origin().queryListByWrapper(Pops.<ResourceCountry>lambdaQuery()
                .from(ResourceCountry.MODEL_MODEL)
                .select(ResourceCountry::getId, ResourceCountry::getCode, ResourceCountry::getName, ResourceCountry::getPhoneCode)
                .isNotNull(ResourceCountry::getPhoneCode));
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (ResourceCountry country : countryList) {
                country.setProvinceList(null);
                country.setRegionList(null);
                country.setMappingList(null);
                country.setOutResourceRelationList(null);
            }
        }
        return countryList;
    }

}
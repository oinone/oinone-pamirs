package pro.shushi.pamirs.resource.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.resource.api.model.ResourceCity;
import pro.shushi.pamirs.resource.api.model.ResourceCityLevel;
import pro.shushi.pamirs.resource.api.service.ResourceCityLevelService;

import java.util.List;
import java.util.Objects;

@Service
@Fun(ResourceCityLevelService.FUN_NAMESPACE)
public class ResourceCityLevelServiceImpl implements ResourceCityLevelService {

    @Function
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceCityLevel> deleteBatch(List<ResourceCityLevel> data) {
        /*
         *
         * 删除城市等级同时把关联的城市删除
         *
         * */
        if (CollectionUtils.isNotEmpty(data)) {
            Models.origin().listFieldQuery(data, ResourceCityLevel::getCityList);
            Models.origin().listRelationDelete(data, ResourceCityLevel::getCityList);
            Models.origin().deleteByPks(data);
        }
        return data;
    }

    @Function
    @Override
    public ResourceCityLevel update(ResourceCityLevel data) {
        data.construct();
        Tx.build().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                data.updateById();
                ResourceCityLevel level = new ResourceCityLevel();
                level.setId(data.getId()).fieldQuery(ResourceCityLevel::getCityList);
                level.relationDelete(ResourceCityLevel::getCityList);
                data.fieldSave(ResourceCityLevel::getCityList);
            }
        });
        return data;
    }

    @Function
    @Override
    public ResourceCityLevel queryOne(ResourceCityLevel query) {
        ResourceCityLevel cityLevel = FetchUtil.fetchOne(query);
        if (Objects.isNull(cityLevel)) {
            return null;
        }
        cityLevel.fieldQuery(ResourceCityLevel::getCityList);
        if (CollectionUtils.isEmpty(cityLevel.getCityList())) {
            return cityLevel;
        }
        new ResourceCity().listFieldQuery(cityLevel.getCityList(), ResourceCity::getProvince);
        return cityLevel;
    }
}

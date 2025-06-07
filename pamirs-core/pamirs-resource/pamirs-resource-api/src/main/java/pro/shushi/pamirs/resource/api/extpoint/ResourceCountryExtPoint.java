package pro.shushi.pamirs.resource.api.extpoint;

import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;
import pro.shushi.pamirs.resource.api.model.ResourceRegion;
import pro.shushi.pamirs.resource.api.model.ResourceRegionMapping;

import java.util.ArrayList;
import java.util.List;

@Ext(ResourceCountry.class)
public class ResourceCountryExtPoint extends AbstractMappingExtPoint<ResourceCountry> {

    @Override
    List<ResourceRegionMapping> getMapping(ResourceCountry resourceCountry) {
        return resourceCountry.getMappingList();
    }

    @Override
    void setMapping(ResourceCountry resourceCountry, List<ResourceRegionMapping> mappings) {
        resourceCountry.setMappingList(mappings);
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public ResourceCountry createBefore(ResourceCountry data) {
        //父类的mapping过滤
        super.createBefore(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public ResourceCountry updateBefore(ResourceCountry data) {
        //父类的mapping过滤
        super.updateBefore(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public ResourceCountry createAfter(ResourceCountry data) {
        ResourceCountry.fetchCurrentRegion(data).create();
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public ResourceCountry updateAfter(ResourceCountry data) {
        ResourceCountry.fetchCurrentRegion(data).updateByCode();
        return data;
    }

    @Override
    @ExtPoint.Implement(priority = 999)
    public List<ResourceCountry> deleteAfter(List<ResourceCountry> data) {
        List<String> regionCodeList = new ArrayList<>();
        for (ResourceCountry item : data)
            regionCodeList.add(item.getCode());
        Models.data().deleteByWrapper(Pops.<ResourceRegion>lambdaQuery()
                .from(ResourceRegion.MODEL_MODEL)
                .in(ResourceRegion::getCode, regionCodeList));
        return data;
    }
}

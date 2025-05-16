package pro.shushi.pamirs.resource.api.extpoint;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.base.extpoint.CreateBeforeExtPoint;
import pro.shushi.pamirs.meta.base.extpoint.UpdateBatchBeforeExtPoint;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;
import pro.shushi.pamirs.resource.api.model.ResourceRegion;

import java.util.List;
import java.util.Optional;

/**
 * @author shier
 * date 2020/4/3
 */
@Ext(ResourceAddress.class)
public class ResourceAddressExtPoint implements CreateBeforeExtPoint<ResourceAddress>, UpdateBatchBeforeExtPoint<ResourceAddress> {

    @Override
    @ExtPoint.Implement(displayName = "创建之前")
    public ResourceAddress createBefore(ResourceAddress address) {
        checkAndSetFullAddress(address);
        return address;
    }

    @Override
    @ExtPoint.Implement(displayName = "创建之前批量")
    public List<ResourceAddress> updateBatchBefore(List<ResourceAddress> data) {
        for (ResourceAddress address : data)
            checkAndSetFullAddress(address);
        return data;
    }

    public static void checkAndSetFullAddress(ResourceAddress address) {
        if (address == null)
            return;
        StringBuilder full = new StringBuilder();
        Optional.ofNullable(address.getCountry()).ifPresent(_countyId -> {
            full.append(Optional.of(_countyId)
                    .map(v -> new ResourceRegion().setId(_countyId).<ResourceRegion>queryOne())
                    .map(ResourceRegion::getName)
                    .orElse("")).append(" ");
        });

        Optional.ofNullable(address.getProvince()).ifPresent(_provinceId -> {
            full.append(Optional.of(_provinceId)
                    .map(v -> new ResourceRegion().setId(_provinceId).<ResourceRegion>queryOne())
                    .map(ResourceRegion::getName)
                    .orElse("")).append(" ");
        });
        Optional.ofNullable(address.getCity()).ifPresent(_cityId -> {
            full.append(Optional.of(_cityId)
                    .map(v -> new ResourceRegion().setId(_cityId).<ResourceRegion>queryOne())
                    .map(ResourceRegion::getName)
                    .orElse("")).append(" ");
        });
        Optional.ofNullable(address.getDistrict()).ifPresent(_districtId -> {
            full.append(Optional.of(_districtId)
                    .map(v -> new ResourceRegion().setId(_districtId).<ResourceRegion>queryOne())
                    .map(ResourceRegion::getName)
                    .orElse("")).append(" ");
        });
        Optional.ofNullable(address.getStreet()).ifPresent(_streetId -> {
            full.append(Optional.of(_streetId)
                    .map(v -> new ResourceRegion().setId(_streetId).<ResourceRegion>queryOne())
                    .map(ResourceRegion::getName)
                    .orElse("")).append(" ");
        });
        String street2 = address.getStreet2();
        if (StringUtils.isNotBlank(street2)) full.append(street2);
        address.setFullAddress(full.toString());
    }

}

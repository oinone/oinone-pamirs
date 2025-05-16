package pro.shushi.pamirs.resource.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;
import pro.shushi.pamirs.resource.api.model.ResourceRegion;
import pro.shushi.pamirs.resource.api.service.ResourceRegionService;
import pro.shushi.pamirs.resource.api.util.RemoteResourceHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants.COUNTRY_CODE;

@Fun(ResourceRegionService.FUN_NAMESPACE)
@Slf4j
@Component
public class ResourceRegionServiceImpl implements ResourceRegionService {

    private final static String REGION = "regionTreeResolve";

    private final static String REGION_FILE_PATH = "region/address.js";

    @Function
    @Override
    public ResourceRegion queryById(Long id) {
        if (id == null) {
            return null;
        }
        return new ResourceRegion().queryById(id);
    }

    @Function
    @Override
    public ResourceRegion queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return new ResourceRegion().queryByCode(code);
    }

    @Function
    @Override
    public ResourceRegion queryByName(String name, Integer level, String pCode) {
        if (StringUtils.isBlank(name) || level == null) {
            return null;
        }
        // TODO 缓存
        ResourceRegion resourceRegion = new ResourceRegion().setName(name).setLevel(level);
        if (StringUtils.isNotBlank(pCode) && level > DefaultResourceConstants.REGION_LEVEL_COUNTRY) {
            resourceRegion.setPCode(pCode);
        }
        resourceRegion = resourceRegion.queryOne();

        return resourceRegion;
    }

    @Override
    @Function
    public String queryRegionCodeByName(ResourceAddress resourceAddress) {
        if (StringUtils.isBlank(resourceAddress.getCountryName())) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_NO_COUNTRY_NAME).errThrow();
        }

        return queryRegionCodeByNameInner(resourceAddress);
    }

    private String queryRegionCodeByNameInner(ResourceAddress resourceAddress) {
        ResourceRegion countryRegion = queryByName(resourceAddress.getCountryName(), DefaultResourceConstants.REGION_LEVEL_COUNTRY, null);
        if (countryRegion == null) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_INVALID_COUNTRY_NAME).errThrow();
        }
        if (StringUtils.isBlank(resourceAddress.getProvinceName())) {
            return countryRegion.getCode();
        }

        ResourceRegion provinceRegion = queryByName(resourceAddress.getProvinceName(), DefaultResourceConstants.REGION_LEVEL_PROVINCE, countryRegion.getCode());
        if (provinceRegion == null) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_INVALID_PROVINCE_NAME).errThrow();
        }
        if (StringUtils.isBlank(resourceAddress.getCityName())) {
            return provinceRegion.getCode();
        }

        ResourceRegion cityRegion = queryByName(resourceAddress.getCityName(), DefaultResourceConstants.REGION_LEVEL_CITY, provinceRegion.getCode());
        if (cityRegion == null) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_INVALID_CITY_NAME).errThrow();
        }
        if (StringUtils.isBlank(resourceAddress.getDistrictName())) {
            return cityRegion.getCode();
        }

        ResourceRegion districtRegion = queryByName(resourceAddress.getDistrictName(), DefaultResourceConstants.REGION_LEVEL_DISTRICT, cityRegion.getCode());
        if (districtRegion == null) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_INVALID_DISTRICT_NAME).errThrow();
        }
        if (StringUtils.isBlank(resourceAddress.getStreetName())) {
            return districtRegion.getCode();
        }

        ResourceRegion streetRegion = queryByName(resourceAddress.getStreetName(), DefaultResourceConstants.REGION_LEVEL_DISTRICT, districtRegion.getCode());
        if (streetRegion == null) {
            throw PamirsException.construct(ExpEnumerate.REGION_QUERY_CODE_INVALID_STREET_NAME).errThrow();
        }
        return streetRegion.getCode();
    }

    //监听Region表的变化，全量刷新数据
    @Function
    @Override
    public ResourceRegion updateRegionFile(ResourceRegion region) {
        List<ResourceRegion> topList = Models.origin().queryListByWrapper(Pops.<ResourceRegion>lambdaQuery()
                .from(ResourceRegion.MODEL_MODEL)
                .isNull(ResourceRegion::getPCode)
                .isNull(ResourceRegion::getPid));
        //把中国放置在列表的第一位
        List<ResourceRegion> chinaList = topList.stream().filter(x -> x.getCode().equals(COUNTRY_CODE)).collect(Collectors.toList());
        topList.removeIf(x -> x.getCode().equals(COUNTRY_CODE));
        CollectionUtils.addAll(chinaList, topList.iterator());
        //向下递归查询子节点
        fetchChildren(chinaList);
        //压缩数据
        compressChildren(chinaList);
        //上传地址文件
        FileClient fileClient = FileClientFactory.getClient();
        if (fileClient == null) {
            throw PamirsException.construct(ExpEnumerate.NOTFOUND_FILE_CLIEND_CONFIG).errThrow();
        }
        //Long 转 String, 防止数字太长导致js精度偏差
        String json = JSON.toJSONString(chinaList,
                new SerializeFilter[]{
                        (ValueFilter) (o, s, value) -> {
                            if (value instanceof Long) {
                                return value.toString();
                            }
                            return value;
                        },
                });
        upload(fileClient, json);
        return region;
    }

    @Function
    @Override
    public ResourceRegion create(ResourceRegion region) {
        return region.create();
    }


    @Function
    @Override
    public ResourceRegion queryOne(ResourceRegion region) {
        return region.queryOne();
    }

    @Function
    @Override
    public ResourceRegion queryOneByWrapper(IWrapper<ResourceRegion> queryWrapper) {
        return new ResourceRegion().queryOneByWrapper(queryWrapper);
    }

    @Function
    @Override
    public List<ResourceRegion> queryListByWrapper(IWrapper<ResourceRegion> queryWrapper) {
        return new ResourceRegion().queryList(queryWrapper);
    }

    @Function
    @Override
    public List<ResourceRegion> queryListByPage(Pagination<ResourceRegion> page, IWrapper<ResourceRegion> queryWrapper) {
        return new ResourceRegion().queryListByWrapper(page, queryWrapper);
    }

    private void fetchChildren(List<ResourceRegion> topList) {
        if (CollectionUtils.isEmpty(topList)) {
            return;
        }
        List<String> codes = Lists.transform(topList, ResourceRegion::getCode);
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        List<ResourceRegion> children = Models.origin().queryListByWrapper(Pops.<ResourceRegion>lambdaQuery()
                .from(ResourceRegion.MODEL_MODEL)
                .in(ResourceRegion::getPCode, codes));
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Map<String, List<ResourceRegion>> childrenMap = children.stream().collect(Collectors.groupingBy(ResourceRegion::getPCode));
        for (ResourceRegion region : topList) {
            List<ResourceRegion> regionList = childrenMap.get(region.getCode());
            if (CollectionUtils.isNotEmpty(regionList)) {
                region.setChildren(regionList);
                fetchChildren(regionList);
            }
        }
    }

    private void compressChildren(List<ResourceRegion> topList) {
        if (CollectionUtils.isEmpty(topList)) {
            return;
        }
        for (ResourceRegion region : topList) {
            if (CollectionUtils.isNotEmpty(region.getChildren())) {
                compressChildren(region.getChildren());
            }
        }
        compressRegionList(topList);
    }

    private void compressRegionList(List<ResourceRegion> topList) {
        if (CollectionUtils.isEmpty(topList)) {
            return;
        }
        for (ResourceRegion region : topList) {
            region.unsetOutResourceRelationList()
                    .unsetSourceType()
                    .unsetCountryCode().unsetCountry()
                    .unsetHasChildren()
                    .unsetLevel()
                    .unsetType()
                    .unsetCreateUid().unsetCreateDate().unsetWriteUid().unsetWriteDate();
            region.get_d().remove(VariableNameConstants.entityModel);
            region.get_d().remove("pamirsBatchSize");
        }
    }

    private void upload(FileClient fileClient, String dataJson) {
        dataJson = REGION + CharacterConstants.LEFT_BRACKET + dataJson + CharacterConstants.RIGHT_BRACKET;
        String downloadUrl = fileClient.uploadByFileName(REGION_FILE_PATH, dataJson.getBytes(StandardCharsets.UTF_8));
        RemoteResourceHelper.DOWNLOAD_URL = downloadUrl;
        log.error("ResourceRegion jsonp path: {}", downloadUrl);
    }

}

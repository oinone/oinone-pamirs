package pro.shushi.pamirs.resource.core.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.resource.api.model.ResourceCountry;
import pro.shushi.pamirs.resource.api.service.ResourceCountryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Nation
 * @cdate 2021-05-06 10:39
 */
@Service
@Fun(ResourceCountryService.FUN_NAMESPACE)
public class ResourceCountryServiceImpl implements ResourceCountryService {

    private static final String KEY = "CACHE:COUNTRY_ALL";

    @Override
    @Function
    public ResourceCountry queryById(Long id) {
        if (id == null) {
            return null;
        }
//        return new ResourceCountry().queryById(id);
        return queryAllIdsMap().get(id);
    }

    @Override
    @Function
    public ResourceCountry queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return queryAllCodesMap().get(code);
//        return new ResourceCountry().queryByCode(code);
    }

    @Override
    @Function
    public List<ResourceCountry> queryListByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return FetchUtil.fetchListByCodes(ResourceCountry.MODEL_MODEL, codes);
    }

    private IWrapper<ResourceCountry> initQueryWrapper(IWrapper<ResourceCountry> queryWrapper) {
        if (queryWrapper instanceof LambdaQueryWrapper) {
            queryWrapper = ((LambdaQueryWrapper<ResourceCountry>) queryWrapper).from(ResourceCountry.MODEL_MODEL);
        } else {
            queryWrapper = ((QueryWrapper<ResourceCountry>) queryWrapper).lambda().from(ResourceCountry.MODEL_MODEL);
        }
        return queryWrapper;
    }

    @Override
    @Function
    public List<ResourceCountry> queryListByWrapper(IWrapper<ResourceCountry> queryWrapper) {
        queryWrapper = initQueryWrapper(queryWrapper);
        return new ResourceCountry().queryList(queryWrapper);
    }

    @Override
    @Function
    public ResourceCountry queryByName(String name) {
        return new ResourceCountry().setName(name).queryOne();
    }

    @Override
    @Function
    public Map<String, ResourceCountry> queryMapByCodes(List<String> codes) {
        List<ResourceCountry> list = queryListByCodes(codes);
        return list.stream().collect(Collectors.toMap(ResourceCountry::getCode, a -> a, (a, b) -> a));
    }

    @Override
    @Function
    public Map<Long, ResourceCountry> queryAllIdsMap() {
        return CACHE.get(KEY).stream().collect(Collectors.toMap(ResourceCountry::getId, a -> a, (a, b) -> a));
    }

    @Override
    @Function
    public Map<String, ResourceCountry> queryAllCodesMap() {
        return CACHE.get(KEY).stream().collect(Collectors.toMap(ResourceCountry::getCode, a -> a, (a, b) -> a));
    }


    private List<ResourceCountry> queryAll() {
        return new ResourceCountry().queryList();
    }

    private LoadingCache<String, List<ResourceCountry>> CACHE = Caffeine.newBuilder()
            .maximumSize(300)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .refreshAfterWrite(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<ResourceCountry>>() {
                @Nullable
                @Override
                public List<ResourceCountry> load(@NonNull String i) {
                    return queryAll();
                }

                @Override
                public List<ResourceCountry> reload(@NonNull String key, @NonNull List<ResourceCountry> oldValue) {
                    return queryAll();
                }
            });
}

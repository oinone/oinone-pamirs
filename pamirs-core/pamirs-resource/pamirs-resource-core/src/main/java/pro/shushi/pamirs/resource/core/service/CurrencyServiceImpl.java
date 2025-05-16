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
import pro.shushi.pamirs.framework.connectors.data.api.orm.BatchSizeHintApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;
import pro.shushi.pamirs.resource.api.service.CurrencyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Nation
 * @cdate 2021-04-02 10:39
 */
@Service
@Fun(CurrencyService.FUN_NAMESPACE)
public class CurrencyServiceImpl implements CurrencyService {

    private static final String KEY = "CACHE:CURRENCY_ALL";

    @Override
    @Function
    public ResourceCurrency queryById(Long id) {
        if (id == null) {
            return null;
        }
        return queryAllIdsMap().get(id);
    }

    @Override
    @Function
    public ResourceCurrency queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return queryAllCodesMap().get(code);
    }

    @Override
    @Function
    public List<ResourceCurrency> queryListByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return FetchUtil.fetchListByCodes(ResourceCurrency.MODEL_MODEL, codes);
    }

    @Override
    @Function
    public Map<String, ResourceCurrency> queryMapByCodes(List<String> codes) {
        List<ResourceCurrency> list = queryListByCodes(codes);
        return list.stream().collect(Collectors.toMap(ResourceCurrency::getCode, a -> a, (a, b) -> a));
    }

    @Override
    @Function
    public Map<Long, ResourceCurrency> queryAllIdsMap() {
        return CACHE.get(KEY).stream().collect(Collectors.toMap(ResourceCurrency::getId, a -> a, (a, b) -> a));
    }

    @Override
    @Function
    public Map<String, ResourceCurrency> queryAllCodesMap() {
        return CACHE.get(KEY).stream().collect(Collectors.toMap(ResourceCurrency::getCode, a -> a, (a, b) -> a));
    }


    private List<ResourceCurrency> queryAll() {
        try (BatchSizeHintApi batchSize = BatchSizeHintApi.use(-1)) {
            return new ResourceCurrency().queryList();
        }
    }

    private LoadingCache<String, List<ResourceCurrency>> CACHE = Caffeine.newBuilder()
            .maximumSize(300)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .refreshAfterWrite(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, List<ResourceCurrency>>() {
                @Nullable
                @Override
                public List<ResourceCurrency> load(@NonNull String i) {
                    return queryAll();
                }

                @Override
                public List<ResourceCurrency> reload(@NonNull String key, @NonNull List<ResourceCurrency> oldValue) {
                    return queryAll();
                }
            });
}

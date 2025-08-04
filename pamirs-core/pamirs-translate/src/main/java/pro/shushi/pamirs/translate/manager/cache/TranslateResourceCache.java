package pro.shushi.pamirs.translate.manager.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_OCTOTHORPE;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 12:06
 */
@Component
public class TranslateResourceCache {

    private final LoadingCache<String, List<ResourceTranslationItem>> cache;

    public TranslateResourceCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .weakValues()
                .build(new CacheLoader<String, List<ResourceTranslationItem>>() {
                    @Override
                    public List<ResourceTranslationItem> load(@Nonnull String modelOrDict /* langCode#modelName(dictName) */) {
                        return Optional.of(modelOrDict)
                                .map(_modelOrDict -> _modelOrDict.split(SEPARATOR_OCTOTHORPE))
                                .filter(_arr -> _arr.length == 2)
                                .map(_arr -> {
                                    String langCode = _arr[0];
                                    String modelDict = _arr[1];
                                    return Models.data()
                                            .queryListByWrapper(Pops.<ResourceTranslationItem>lambdaQuery()
                                                            .from(ResourceTranslationItem.MODEL_MODEL)
                                                            .eq(ResourceTranslationItem::getLangCode, langCode)
                                                            .eq(ResourceTranslationItem::getModel, modelDict)
//                                                    .in(ResourceTranslationItem::getTranslateFor, TranslateForEnum.BACK_END, TranslateForEnum.BACK_END_DATA)
                                            );
                                })
                                .orElse(Collections.emptyList());
                    }
                });
    }

    public List<ResourceTranslationItem> getCache(String key) {
        return cache.get(key);
    }
}

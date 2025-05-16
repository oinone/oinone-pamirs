package pro.shushi.pamirs.translate.manager.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Adamancy Zhang
 * @date 2021-01-23 13:50
 */
@Component
public class TranslateModelFieldCache {

    private final LoadingCache<String, List<String>> cache;

    public TranslateModelFieldCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .weakValues().build(s -> {
                    ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(s);
                    if (modelConfig == null) {
                        return Collections.emptyList();
                    }
                    List<String> fields = new ArrayList<>();
                    for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
                        ModelField modelField = modelFieldConfig.getModelField();
                        if (isNeedTranslate(modelField)) {
                            fields.add(modelField.getLname());
                        }
                    }
                    if (fields.isEmpty()) {
                        return Collections.emptyList();
                    }
                    return fields;
                });
    }

    public List<String> getCache(String key) {
        return cache.get(key);
    }

    private boolean isNeedTranslate(ModelField modelField) {
        Boolean isTranslate = modelField.getTranslate();
        if (isTranslate != null && isTranslate) {
            String ttype = modelField.getTtype().value();
            if (TtypeEnum.RELATED.value().equals(ttype)) {
                ttype = modelField.getRelatedTtype().value();
            }
            return TtypeEnum.STRING.value().equals(ttype);
        }
        return false;
    }
}

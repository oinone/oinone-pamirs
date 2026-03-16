package pro.shushi.pamirs.translate.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.core.common.cache.UnsafeCache;
import pro.shushi.pamirs.core.common.cache.ValueGenerator;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.manager.cache.TranslateModelFieldCache;
import pro.shushi.pamirs.translate.manager.cache.TranslateResourceCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.SEPARATOR_OCTOTHORPE;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 12:03
 */
@Component
@Order(0)
@SPI.Service
public class DefaultTranslateService implements TranslateService {

    @Autowired
    private TranslateModelFieldCache translateFieldCache;

    @Autowired
    private TranslateResourceCache translateResourceCache;

    private final ValueGenerator<String, MemoryIterableSearchCache<String, ResourceTranslationItem>> getNewInstanceFunction;

    public DefaultTranslateService() {
        this.getNewInstanceFunction = key -> {
            List<ResourceTranslationItem> cacheList = this.translateResourceCache.getCache(key);
            if (CollectionUtils.isEmpty(cacheList)) {
                return null;
            }
            return new MemoryIterableSearchCache<>(cacheList, ResourceTranslationItem::getOrigin);
        };
    }

    @Override
    public Boolean needTranslate() {
        String lang = getCurrentLang();

        // 都是中文：不翻译
        if (StringUtils.equalsIgnoreCase(lang, DefaultResourceConstants.CHINESE_LANGUAGE_CODE)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public String getCurrentLang() {
        String lang = PamirsSession.getLang();
        if (StringUtils.isBlank(lang)) {
            Locale locale = I18nUtils.getLocale();
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(language) && StringUtils.isBlank(country)) {
                // 兼容语言初始化
                country = Locale.SIMPLIFIED_CHINESE.getCountry();
            } else if (Locale.US.getLanguage().equals(language) && StringUtils.isBlank(country)) {
                // 兼容语言初始化
                country = Locale.US.getCountry();
            }
            if (StringUtils.isBlank(country)) {
                return language;
            }
            return language + CharacterConstants.SEPARATOR_HYPHEN + country;
        }
        return lang;
    }

    @Override
    public String getCurrentLangIsoCode() {
        String lang = PamirsSession.getLang();
        if (StringUtils.isBlank(lang)) {
            Locale locale = I18nUtils.getLocale();
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(language) && StringUtils.isBlank(country)) {
                // 兼容语言初始化
                country = Locale.SIMPLIFIED_CHINESE.getCountry();
            } else if (Locale.US.getLanguage().equals(language) && StringUtils.isNotBlank(country)) {
                // 兼容语言初始化
                country = null;
            }
            if (StringUtils.isBlank(country)) {
                return language;
            }
            return language + CharacterConstants.SEPARATOR_UNDERLINE + country;
        } else if (DefaultResourceConstants.ENGLISH_LANGUAGE.getCode().equals(lang)) {
            // 兼容语言初始化
            return DefaultResourceConstants.ENGLISH_LANGUAGE.getIsoCode();
        } else if (DefaultResourceConstants.CHINESE_LANGUAGE.getCode().equals(lang)) {
            // 兼容语言初始化
            return DefaultResourceConstants.CHINESE_LANGUAGE.getIsoCode();
        } else {
            // 强制使用标准 locale 格式
            lang = lang.replaceAll("-", "_");
        }
        return lang;
    }

    @Override
    public <T extends ModelField> void translateModelFields(String lang, List<T> modelFields) {
        if (!needTranslate()) {
            return;
        }
        UnsafeCache<String, MemoryIterableSearchCache<String, ResourceTranslationItem>> dictionaryCache = new UnsafeCache<>(modelFields.size(), getNewInstanceFunction);
        MemoryIterableSearchCache<String, ResourceTranslationItem> modelFieldCache = this.getNewInstanceFunction.apply(lang + SEPARATOR_OCTOTHORPE + ModelField.MODEL_MODEL);
        for (ModelField modelField : modelFields) {
            if (TtypeEnum.ENUM.value().equals(modelField.getTtype().value())) {
                final String dictionary = modelField.getDictionary();
                final MemoryIterableSearchCache<String, ResourceTranslationItem> cache = dictionaryCache.get(lang + SEPARATOR_OCTOTHORPE + dictionary);
                if (cache != null) {
                    List<DataDictionaryItem> dictionaryItems = modelField.getOptions();
                    if (CollectionUtils.isNotEmpty(dictionaryItems)) {
                        for (DataDictionaryItem dictionaryItem : dictionaryItems) {
                            ResourceTranslationItem translationItem = cache.get(dictionaryItem.getDisplayName());
                            if (translationItem != null) {
                                dictionaryItem.setDisplayName(translationItem.getTarget());
                            }
                        }
                        modelField.setOptions(dictionaryItems);
                    }
                }
            }
            if (modelFieldCache != null) {
                ResourceTranslationItem translationItem = modelFieldCache.get(modelField.getDisplayName());
                if (translationItem != null) {
                    modelField.setDisplayName(translationItem.getTarget());
                }
            }
        }
    }


    @Override
    public <T extends DataDictionary> T translateDictionary(T dataDictionary) {
        if (!needTranslate()) {
            return dataDictionary;
        }
        if (null == dataDictionary) {
            return dataDictionary;
        }
        T copy = KryoUtils.get().copy(dataDictionary);
        String lang = getCurrentLang();
        UnsafeCache<String, MemoryIterableSearchCache<String, ResourceTranslationItem>> dictionaryCache = new UnsafeCache<>(1, getNewInstanceFunction);
        MemoryIterableSearchCache<String, ResourceTranslationItem> modelFieldCache = this.getNewInstanceFunction.apply(lang + SEPARATOR_OCTOTHORPE + ModelField.MODEL_MODEL);
        final String dictionary = copy.getDictionary();
        final MemoryIterableSearchCache<String, ResourceTranslationItem> cache = dictionaryCache.get(lang + SEPARATOR_OCTOTHORPE + dictionary);
        if (cache != null) {
            List<DataDictionaryItem> dictionaryItems = copy.getOptions();
            if (CollectionUtils.isNotEmpty(dictionaryItems)) {
                for (DataDictionaryItem dictionaryItem : dictionaryItems) {
                    ResourceTranslationItem translationItem = cache.get(dictionaryItem.getDisplayName());
                    if (translationItem != null) {
                        dictionaryItem.setDisplayName(translationItem.getTarget());
                    }
                }
                copy.setOptions(dictionaryItems);
            }
        }
        if (modelFieldCache != null) {
            ResourceTranslationItem translationItem = modelFieldCache.get(copy.getDisplayName());
            if (translationItem != null) {
                copy.setDisplayName(translationItem.getTarget());
            }
        }
        return copy;
    }

    @Override
    public <T> void simpleTranslate(String lang, List<T> list, Function<T, String> getter, BiConsumer<T, String> setter, String... models) {
        if (!needTranslate()) {
            return;
        }

        List<MemoryIterableSearchCache<String, ResourceTranslationItem>> cacheList = new ArrayList<>();
        boolean isTranslate = false;
        for (String model : models) {
            List<ResourceTranslationItem> translationItems = this.translateResourceCache.getCache(lang + SEPARATOR_OCTOTHORPE + model);
            if (CollectionUtils.isEmpty(translationItems)) {
                cacheList.add(null);
                continue;
            }
            isTranslate = true;
            cacheList.add(new MemoryIterableSearchCache<>(translationItems, ResourceTranslationItem::getOrigin));
        }
        if (!isTranslate) {
            return;
        }
        for (T item : list) {
            for (MemoryIterableSearchCache<String, ResourceTranslationItem> cache : cacheList) {
                if (cache == null) {
                    continue;
                }
                String originValue = getter.apply(item);
                if (originValue == null) {
                    continue;
                }
                ResourceTranslationItem translationItem = cache.get(originValue);
                if (translationItem == null) {
                    continue;
                }
//                if (translateBefore != null) {
//                    if (!translateBefore.process(item, translationItem, cache)) {
//                        continue;
//                    }
//                }
                setter.accept(item, translationItem.getTarget());
//                if (translateAfter != null) {
//                    translateAfter.process(item, translationItem, cache);
//                }
            }
        }
    }

    @Override
    public void generalDataTranslate(String lang, List<?> list, String model) {
        if (!needTranslate()) {
            return;
        }
        List<String> fields = translateFieldCache.getCache(model);
        if (fields.isEmpty()) {
            return;
        }
        List<ResourceTranslationItem> translationItems = this.translateResourceCache.getCache(lang + SEPARATOR_OCTOTHORPE + model);
        if (CollectionUtils.isEmpty(translationItems)) {
            return;
        }
        MemoryIterableSearchCache<String, ResourceTranslationItem> cache = new MemoryIterableSearchCache<>(translationItems, ResourceTranslationItem::getOrigin);
        for (Object item : list) {
            if (item != null) {
                for (String field : fields) {
                    Object objectValue = FieldUtils.getFieldValue(item, field);
                    if (objectValue == null) {
                        continue;
                    }
                    String stringValue = StringHelper.valueOf(objectValue);
                    ResourceTranslationItem translationItem = cache.get(stringValue);
                    if (translationItem != null) {
                        FieldUtils.setFieldValue(item, field, translationItem.getTarget());
                    }
                }
            }
        }
    }

//    private <T> void dynamicTranslate(String lang, List<T> list, Function<T, String> cacheKeyGetter, Function<T, String> getter, BiConsumer<T, String> setter) {
//
//    }

//    /**
//     * 翻译前置钩子
//     *
//     * @param <T> 翻译源类型
//     */
//    @FunctionalInterface
//    public interface TranslateBeforeHook<T> {
//
//        /**
//         * 翻译前处理
//         *
//         * @param item            当前翻译源
//         * @param translationItem 当前翻译项
//         * @param cache           翻译项缓存
//         * @return 是否中断翻译
//         */
//        boolean process(T item, ResourceTranslationItem translationItem, MemoryIterableSearchCache<ResourceTranslationItem> cache);
//    }
//
//    /**
//     * 翻译后置钩子
//     *
//     * @param <T> 翻译源类型
//     */
//    @FunctionalInterface
//    public interface TranslateAfterHook<T> {
//
//        /**
//         * 翻译前处理
//         *
//         * @param item            当前翻译源
//         * @param translationItem 当前翻译项
//         * @param cache           翻译项缓存
//         */
//        void process(T item, ResourceTranslationItem translationItem, MemoryIterableSearchCache<ResourceTranslationItem> cache);
//    }
}

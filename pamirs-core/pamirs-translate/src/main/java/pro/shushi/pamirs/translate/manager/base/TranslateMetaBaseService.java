package pro.shushi.pamirs.translate.manager.base;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.resource.api.enmu.TranslateForEnum;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.action.ResourceTranslationAction;
import pro.shushi.pamirs.translate.manager.DataDictionaryTranslateServiceImpl;
import pro.shushi.pamirs.translate.manager.cache.TranslateL2CacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 获取元数据
 *
 * @author xzf 2022/12/16 17:00
 **/
public interface TranslateMetaBaseService<T extends MetaBaseModel> {

    Logger log = LoggerFactory.getLogger(ResourceTranslationAction.class);

    String RES_LANG_CODE = "zh-CN";
    String LANG_CODE = "en-US";
    String METADATA_SPLIT = "#X#";

    Pattern zhCNOriginP = Pattern.compile("[\\u4e00-\\u9fa5]+");

    /**
     * 只转换了基础类型的数据，如果要操作是关联模型或者枚举等数据，需要自行dataConverter一把
     *
     * @param model String
     * @return List<T>
     * @see DataDictionaryTranslateServiceImpl
     */
    default <Q extends MetaBaseModel> List<Q> queryTotalFromDB(String model) {

        long start1 = System.currentTimeMillis();

        QueryWrapper<DataMap> wrapper = Pops.<DataMap>query().from(model);

        GenericMapper genericMapper = BeanDefinitionUtils.getBean(GenericMapper.class);
        long count = genericMapper.selectCount(wrapper);
        int totalPage = new Long(count / 1000L).intValue() + 1;

        List<DataMap> mapList = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            Pagination<DataMap> page = new Pagination<>();
            page.setModel(model);
            page.setCurrentPage(i);
            page.setSize(1000L);
            List<DataMap> _mapList = genericMapper.selectListByPage(page, wrapper);
            if (CollectionUtils.isNotEmpty(_mapList)) {
                mapList.addAll(_mapList);
            }
        }

        long start2 = System.currentTimeMillis();

        log.info("翻译元数据查询结果, 模型：{} 数量: {}, 查询数据耗时:{}", model, mapList.size(), (start2 - start1) + "ms");

        return BeanDefinitionUtils.getBean(DataConverter.class).out(model, mapList);
    }

    default String getModule(String model) {
        ModelConfig modelConfig = null;
        try {
            modelConfig = PamirsSession.getContext().getModelConfig(model);
        } catch (Throwable exp) {
            log.error("获取模型异常: {}", model);
        }

        return null == modelConfig ? "" : modelConfig.getModule();
    }


    default List<ResourceTranslationItem> fetchMetaDataPojo() {

        long start1 = System.currentTimeMillis();
        String initModel = initModelType();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(initModel);
        List<ModelFieldConfig> fieldConfigList = modelConfig.getModelFieldConfigList();
        Set<ModelField> translateFieldSet = new HashSet<>();
        for (ModelFieldConfig fieldConfig : fieldConfigList) {
            if (null == fieldConfig) {
                continue;
            }
            Boolean isTranslate = fieldConfig.getModelField().getTranslate();
            if (null == isTranslate || !isTranslate) {
                continue;
            }
            translateFieldSet.add(fieldConfig.getModelField());
        }

        List<ResourceTranslationItem> itemList = new ArrayList<>();
        Set<String> unique = new HashSet<>();
        for (T data : Optional.ofNullable(this.<T>queryTotalFromDB(initModel)).orElse(Collections.emptyList())) {
            for (ModelField translateField : translateFieldSet) {
                String ttype = translateField.getTtype().value();

                String willTranslate = null;
                switch (ttype) {
                    case "string":
                    case "text":
                    case "html":
                    case "phone":
                    case "email":
                        willTranslate = (String) data.get_d().get(translateField.getField());
                        if (willTranslate != null && willTranslate.contains("internalGoto")) {
                            willTranslate = null;
                        }
                        break;
//                    case "enum":
//                        Object valueObj = data.get_d().get(translateField.getField());
//                        valueObj = a.AUtil.getValue(valueObj, "value");
//                        willTranslate = String.valueOf(valueObj);
//                        break;
                    default:
                        continue;
                }
                if (StringUtils.isBlank(willTranslate)) {
                    continue;
                }

                if (unique.contains(willTranslate)) {
                    continue;
                }

                unique.add(willTranslate);

                ResourceTranslationItem item = new ResourceTranslationItem();
                item.setModule(ModuleConstants.MODULE_BASE);
                item.setModel(initModel);
                item.setResLangCode(RES_LANG_CODE);
                item.setLangCode(LANG_CODE);
                item.setOrigin(willTranslate);
                itemList.add(item);
            }
        }

        long start2 = System.currentTimeMillis();

        log.info("翻译项数据计算,耗时:{}", (start2 - start1) + "ms");

        return itemList;
    }

    Set<String> modelType();

    String initModelType();

    static String buildDefaultItemUnique(String module, String model, String resLangCode, String langCode, String origin) {
        if (StringUtils.isBlank(module) || StringUtils.isBlank(model) || StringUtils.isBlank(origin)) {
//            System.out.println("有空数据:" + "module:" + module + ",model:" + model + ",origin:" + origin);
            return "";
        }

        return StringUtils.joinWith(METADATA_SPLIT, module, model, resLangCode, langCode, origin);
    }

    @Data
    class ItemUniquePojo {

        private String itemUnqiue;
        private String translationUnique;
        private String module;
        private String model;
        private String resLangCode;
        private String langCode;
        private String origin;


        public ItemUniquePojo() {

        }

        public ResourceTranslationItem buildItem(String itemUnique) {
            this.itemUnqiue = itemUnique;
            String[] split = itemUnique.split(METADATA_SPLIT);
            this.module = split[0];
            this.model = split[1];
            this.resLangCode = split[2];
            this.langCode = split[3];
            this.origin = split[4];

            ResourceTranslationItem item = new ResourceTranslationItem();
            item.setModule(module);
            item.setModel(model);
            item.setOrigin(origin);

            item.setResLangCode(resLangCode);
            item.setLangCode(langCode);
            item.setState(Boolean.FALSE);//翻译想默认给他未激活状态，让用户自己去点
            return item;
        }

        public ResourceTranslation buildTranslation(String translationUnique) {
            this.translationUnique = translationUnique;
            String[] split = translationUnique.split(METADATA_SPLIT);
            this.module = split[0];
            this.model = split[1];
            this.resLangCode = split[2];
            this.langCode = split[3];
            ResourceTranslation translation = new ResourceTranslation();
            translation.setModule(module);
            translation.setModel(model);

            translation.setResLangCode(resLangCode);
            translation.setLangCode(langCode);
            translation.setState(Boolean.TRUE);//翻译默认给他激活状态
            return translation;
        }

    }

    static String calcDefaultTranslateUnique(String itemUnique) {
        int originIndexBegin = itemUnique.lastIndexOf(METADATA_SPLIT);
        if (originIndexBegin < 0) {
            return "";
        }
        String translateUnique = itemUnique.substring(0, originIndexBegin);
        return translateUnique;
    }

    Function<ResourceTranslation, String> uniqueTranslateFunction = data -> {
        String module = data.getModule();
//        String model       = data.getModel();
        String resLangCode = data.getResLangCode();
        String langCode = data.getLangCode();
        //model
        String unique = StringUtils.joinWith(METADATA_SPLIT, module, resLangCode, langCode);
        return unique;
    };

    Function<ResourceTranslationItem, String> uniqueItemFunction = data -> {
        String module = data.getModule();
        String model = data.getModel();
        String origin = data.getOrigin();
        String resLangCode = data.getResLangCode();
        String langCode = data.getLangCode();
        return StringUtils.joinWith(METADATA_SPLIT, module, model, resLangCode, langCode, origin);
    };

    static String calcTarget(String module, String model, String resLangCode, String langCode, String origin) {
        return BeanDefinitionUtils.getBean(TranslateL2CacheManager.class).getItemCache(module, resLangCode, langCode, model, origin);
    }
}


// 1. 元数据存储到翻译项
// 2. 导出、导入翻译项
// 3. 元数据翻译的时候过滤
// 4. i18n.js 生成，传达oss，给前端调用
package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.*;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class DataDictionaryTranslateServiceImpl implements TranslateMetaBaseService<DataDictionary> {

    @Override
    public List<ResourceTranslationItem> fetchMetaDataPojo() {
        long start1 = System.currentTimeMillis();
        String initModel = initModelType();
        ModelConfig dataCfg = PamirsSession.getContext().getModelConfig(initModel);
        List<ModelFieldConfig> fieldConfigList = dataCfg.getModelFieldConfigList();
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

        ModelConfig itemCfg = PamirsSession.getContext().getModelConfig(initModel);
        List<ModelFieldConfig> itemCfgList = itemCfg.getModelFieldConfigList();
        Set<ModelField> itemCfgFieldSet = new HashSet<>();
        for (ModelFieldConfig fieldConfig : itemCfgList) {
            if (null == fieldConfig) {
                continue;
            }
            Boolean isTranslate = fieldConfig.getModelField().getTranslate();
            if (null == isTranslate || !isTranslate) {
                continue;
            }
            itemCfgFieldSet.add(fieldConfig.getModelField());
        }

        Set<String> unique0 = new HashSet<>();
        List<ResourceTranslationItem> itemList = new ArrayList<>();
        for (DataDictionary data : Optional.ofNullable(this.<DataDictionary>queryTotalFromDB(DataDictionary.MODEL_MODEL)).orElse(Collections.emptyList())) {
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
                        break;
                    default:
                        continue;
                }

                if (StringUtils.isBlank(willTranslate)) {
                    continue;
                }

                if (unique0.contains(willTranslate)) {
                    continue;
                }

                unique0.add(willTranslate);

                ResourceTranslationItem item = new ResourceTranslationItem();
                item.setModule(ModuleConstants.MODULE_BASE);
                item.setModel(initModel);
                item.setResLangCode(RES_LANG_CODE);
                item.setLangCode(LANG_CODE);
                item.setOrigin(willTranslate);
                itemList.add(item);
            }

            for (DataDictionaryItem distItem : Optional.ofNullable(data.getOptions()).orElse(Collections.emptyList())) {
                for (ModelField translateField : itemCfgFieldSet) {
                    String ttype = translateField.getTtype().value();

                    String willTranslate = null;
                    switch (ttype) {
                        case "string":
                        case "text":
                        case "html":
                        case "phone":
                        case "email":
                            willTranslate = (String) distItem.get_d().get(translateField.getField());
                            break;
                        default:
                            continue;
                    }

                    if (StringUtils.isBlank(willTranslate)) {
                        continue;
                    }

                    if (unique0.contains(willTranslate)) {
                        continue;
                    }

                    unique0.add(willTranslate);

                    ResourceTranslationItem item = new ResourceTranslationItem();
                    item.setModule(ModuleConstants.MODULE_BASE);
                    item.setModel(initModel);
                    item.setResLangCode(RES_LANG_CODE);
                    item.setLangCode(LANG_CODE);
                    item.setOrigin(willTranslate);
                    itemList.add(item);
                }
            }
        }

        long start2 = System.currentTimeMillis();

        log.info("Translation item data calculation, cost: {}", (start2 - start1) + "ms");

        return itemList;
    }

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                DataDictionary.MODEL_MODEL,
                "designer.DesignerDataDictionary"
        );
    }

    @Override
    public String initModelType() {
        return DataDictionary.MODEL_MODEL;
    }
}

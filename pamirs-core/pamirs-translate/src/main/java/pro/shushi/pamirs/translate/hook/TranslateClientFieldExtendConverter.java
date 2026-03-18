package pro.shushi.pamirs.translate.hook;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.spi.ClientFieldExtendConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static pro.shushi.pamirs.translate.utils.TranslatePlaceholder.placeholder;

/**
 * TranslateClientFieldExtendConverter
 *
 * @author yakir on 2023/09/21 18:47.
 */
@Slf4j
@Order
@Component
public class TranslateClientFieldExtendConverter implements ClientFieldExtendConverter {

    private static Set<String> SPEC_MODELS = Sets.newHashSet(
            ModelField.MODEL_MODEL
    );

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {
        // do nothing ...
    }

    @Override
    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> data) {

        try {
            if (null == data) {
                return;
            }

            TranslateService translateService = TranslateServiceHolder.get();

            if (!translateService.needTranslate()) {
                return;
            }

            ModelField modelField = fieldConfig.getModelField();
            if (!modelField.getTranslate()) {
                return;
            }

            String tType = modelField.getTtype().value();
            if (TtypeEnum.isRelatedType(tType)) {
                tType = modelField.getRelatedTtype().value();
            }
            if (!TtypeEnum.STRING.value().equalsIgnoreCase(tType) && !TtypeEnum.ENUM.value().equalsIgnoreCase(tType)) {
                return;
            }

            String model = null;
            String origin = null;
            String target = null;
            String key = fieldConfig.getField();
            switch (tType) {
                case "string":
                    model = finder(fieldConfig.getModel());
                    if (DataDictionary.MODEL_MODEL.equalsIgnoreCase(model)) {
                        String module = Optional.ofNullable(data.get("module"))
                                .map(TypeUtils::stringValueOf)
                                .orElse(null);
                        if (null == module) {
                            return;
                        }
                        String dictionary = Optional.ofNullable(data.get("dictionary"))
                                .map(TypeUtils::stringValueOf)
                                .orElse(null);
                        if (null == dictionary) {
                            return;
                        }
                        target = placeholder(origin);
                    } else if (DataDictionaryItem.MODEL_MODEL.equalsIgnoreCase(model)) {
//                    String         dict     = fieldConfig.getDictionary();
//                    DataDictionary dataDict = PamirsSession.getContext().getDictionary(dict);
//                    target = TranslateMetaBaseService.calcTarget(dataDict.getModule(), dict, RES_LANG_CODE, lang, origin);
                    } else {
                        Object value = data.get(key);
                        if (NullValue.INSTANCE.equals(value)) {
                            value = null;
                        }
                        origin = (String) value;
                        target = placeholder(origin);
                    }
                    if (StringUtils.isNotBlank(target)) {
                        data.put(key, target);
                    }
                    break;
//                case "enum":
//                    origin = Optional.ofNullable(UnsafeUtil.getValue(data.get(key), "displayName")).map(TypeUtils::stringValueOf).orElse(null);
//                    target = placeholder(origin);
//                    if (StringUtils.isNotBlank(target)) {
//                        UnsafeUtil.setValue(data.get(key), "displayName", target);
//                    }
//                    break;
                default:
                    // no match ...
            }

            log.debug("displayName: [{}] translate:[{}]", fieldConfig.getDisplayName(), fieldConfig.getModelField().getTranslate());
        } catch (Throwable throwable) {
            log.error("Translation exception", throwable);
        }
    }

    // 假设第一个模型一定不是抽象模型
    private String finder(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (ModelTypeEnum.TRANSIENT.value().equalsIgnoreCase(modelConfig.getType().value())) {
            return model;
        }

        ModelConfig superModel = PamirsSession.getContext().getSimpleModelConfig(modelConfig.getSuperModels().get(0));

        String sModel = superModel.getModel();
        if (SPEC_MODELS.contains(sModel)) {
            return sModel;
        }

        if (ModelTypeEnum.ABSTRACT.value().equalsIgnoreCase(superModel.getType().value())) {
            return modelConfig.getModel();
        } else {
            return finder(sModel);
        }
    }
}

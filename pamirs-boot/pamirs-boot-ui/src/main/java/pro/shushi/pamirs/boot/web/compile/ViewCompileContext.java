package pro.shushi.pamirs.boot.web.compile;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 视图编译上下文
 *
 * @author Adamancy Zhang at 16:30 on 2025-04-14
 */
@Slf4j
public class ViewCompileContext {

    private final String model;

    private final Map<String, Map<String, UIField>> virtualFields;

    private final Map<String, Map<String, ModelFieldConfig>> virtualFieldConfigs;

    public ViewCompileContext(String model) {
        this(model, new HashMap<>(), new HashMap<>());
    }

    private ViewCompileContext(String model,
                               Map<String, Map<String, UIField>> virtualFields,
                               Map<String, Map<String, ModelFieldConfig>> virtualFieldConfigs) {
        this.model = model;
        this.virtualFields = virtualFields;
        this.virtualFieldConfigs = virtualFieldConfigs;
    }

    public String getModel() {
        return model;
    }

    public void putVirtualField(String model, String field, UIField uiField) {
        uiField.setModel(model);
        virtualFields.computeIfAbsent(model, k -> new HashMap<>()).put(field, uiField);
    }

    public ModelFieldConfig getVirtualField(String model, String field) {
        Map<String, UIField> modelFields = virtualFields.get(model);
        if (modelFields == null) {
            return null;
        }
        UIField virtualField = modelFields.get(field);
        if (virtualField == null) {
            return null;
        }
        Map<String, ModelFieldConfig> modelFieldConfigMap = virtualFieldConfigs.computeIfAbsent(model, k -> new HashMap<>());
        ModelFieldConfig modelFieldConfig = modelFieldConfigMap.get(field);
        if (modelFieldConfig == null) {
            modelFieldConfig = makeVirtualFieldConfig(virtualField);
            modelFieldConfigMap.put(field, modelFieldConfig);
        }
        return modelFieldConfig;
    }

    private ModelFieldConfig makeVirtualFieldConfig(UIField uiField) {
        ModelField modelField = new ModelField();
        String model = uiField.getModel();
        String data = uiField.getData();
        if (StringUtils.isAnyBlank(model, data)) {
            return null;
        }
        TtypeEnum ttype = uiField.getTtype();
        if (ttype == null) {
            return null;
        }
        Boolean multi = Optional.ofNullable(uiField.getMulti()).orElse(Boolean.FALSE);
        TtypeEnum exactTtype = ttype;
        List<String> related = uiField.getRelated();
        ModelFieldConfig relatedFieldConfig = null;
        boolean isRelated = false;
        if (TtypeEnum.RELATED.equals(ttype)) {
            if (CollectionUtils.isNotEmpty(related)) {
                relatedFieldConfig = getRelatedFieldConfig(model, related);
            }
            if (relatedFieldConfig == null) {
                if (log.isWarnEnabled()) {
                    log.warn("无效的Related字段定义 field: {}, related: {}", data, related);
                }
                return null;
            }
            TtypeEnum relatedTtype = relatedFieldConfig.getModelField().getExactTtype();
            exactTtype = relatedTtype;
            modelField.setRelatedTtype(relatedTtype);
            modelField.setReferences(relatedFieldConfig.getModel());
            modelField.setRelated(related);
            isRelated = true;
        }
        if (TtypeEnum.ENUM.equals(exactTtype)) {
            if (relatedFieldConfig == null) {
                String dictionary = uiField.getDictionary();
                if (StringUtils.isBlank(dictionary)) {
                    if (log.isWarnEnabled()) {
                        log.warn("数据字典编码未正确指定 field: {}", data);
                    }
                    return null;
                }
                modelField.setDictionary(dictionary);
                DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
                if (dataDictionary == null) {
                    if (log.isWarnEnabled()) {
                        log.warn("找不到有效的数据字典 field: {}, dictionary: {}", data, dictionary);
                    }
                    return null;
                }
                modelField.setOptions(dataDictionary.getOptions());
                if (multi) {
                    TtypeEnum valueType = dataDictionary.getValueType();
                    if (TtypeEnum.STRING.equals(valueType)) {
                        modelField.setLtype(List.class.getName());
                        modelField.setLtypeT(String.class.getName());
                    } else if (TtypeEnum.INTEGER.equals(valueType)) {
                        if (dataDictionary.getBit()) {
                            modelField.setLtype(Long.class.getName());
                        } else {
                            modelField.setLtype(List.class.getName());
                            modelField.setLtypeT(Long.class.getName());
                        }
                    }
                } else {
                    TtypeEnum valueType = dataDictionary.getValueType();
                    if (TtypeEnum.STRING.equals(valueType)) {
                        modelField.setLtype(String.class.getName());
                    } else if (TtypeEnum.INTEGER.equals(valueType)) {
                        if (dataDictionary.getBit()) {
                            modelField.setLtype(Long.class.getName());
                        } else {
                            modelField.setLtype(Integer.class.getName());
                        }
                    }
                }
            } else {
                modelField.setDictionary(relatedFieldConfig.getDictionary());
                modelField.setOptions(relatedFieldConfig.getOptions());
                modelField.setLtype(relatedFieldConfig.getLtype());
                modelField.setLtypeT(relatedFieldConfig.getLtypeT());
            }
        } else if (TtypeEnum.isRelationType(exactTtype)) {
            modelField.setReferences(uiField.getReferences());
            if (!isRelated) {
                modelField.setRelationFields(uiField.getRelationFields());
                modelField.setReferenceFields(uiField.getReferenceFields());
            }
            modelField.setStore(Optional.ofNullable(uiField.getStore()).orElse(Boolean.FALSE));
            modelField.setRelationStore(Optional.ofNullable(uiField.getStore()).orElse(Boolean.TRUE));
        } else {
            modelField.setStore(Optional.ofNullable(uiField.getStore()).orElse(Boolean.TRUE));
            modelField.setRelationStore(Optional.ofNullable(uiField.getStore()).orElse(Boolean.FALSE));
        }
        modelField.setModel(model);
        modelField.setName(data);
        modelField.setField(data);
        modelField.setLname(data);
        modelField.setTtype(ttype);
        modelField.setMulti(multi);
        modelField.setOnlyColumn(Boolean.TRUE);
        modelField.setInsertStrategy(FieldStrategyEnum.DEFAULT);
        modelField.setBatchStrategy(FieldStrategyEnum.NOT_CHANGE);
        modelField.setUpdateStrategy(FieldStrategyEnum.DEFAULT);
        modelField.setWhereStrategy(FieldStrategyEnum.DEFAULT);
        ModelFieldConfig modelFieldConfig = new ModelFieldConfig(modelField);
        modelFieldConfig.setIsVirtual(Boolean.TRUE);
        return modelFieldConfig;
    }

    private ModelFieldConfig getRelatedFieldConfig(String model, List<String> relatedFields) {
        String currentModel = model;
        ModelFieldConfig relatedFieldConfig = null;
        for (int i = 0; i < relatedFields.size(); i++) {
            String relatedField = relatedFields.get(i);
            relatedFieldConfig = PamirsSession.getContext().getModelField(currentModel, relatedField);
            if (relatedFieldConfig == null) {
                relatedFieldConfig = getVirtualField(currentModel, relatedField);
                if (relatedFieldConfig == null) {
                    return null;
                }
            }
            if (i == relatedFields.size() - 1) {
                break;
            }
            String references = relatedFieldConfig.getReferences();
            if (StringUtils.isBlank(references)) {
                return null;
            }
            ModelConfig referenceModelConfig = PamirsSession.getContext().getSimpleModelConfig(references);
            if (referenceModelConfig == null) {
                return null;
            }
            currentModel = referenceModelConfig.getModel();
        }
        return relatedFieldConfig;
    }

    public ViewCompileContext transfer(String model) {
        ViewCompileContext compileContext = new ViewCompileContext(model, virtualFields, virtualFieldConfigs);
        return compileContext;
    }
}

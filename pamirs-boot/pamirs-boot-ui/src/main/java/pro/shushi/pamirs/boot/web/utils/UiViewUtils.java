package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.boot.base.ux.model.metadata.UIDictionary;
import pro.shushi.pamirs.boot.base.ux.model.metadata.UIMetadata;
import pro.shushi.pamirs.boot.base.ux.model.part.UIOption;
import pro.shushi.pamirs.boot.base.ux.model.view.UIField;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UI视图工具类
 * <p>
 * 2022/5/7 2:54 下午
 *
 * @author d@shushi.pro
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class UiViewUtils {

    public static void fillOptions(ModelField modelField,
                                   UIView uiView,
                                   UIField uiField,
                                   java.util.function.Function<String, ModelDefinition> modelDefinitionSupplier,
                                   BiFunction<String, String, ModelField> modelFieldSupplier) {
        TtypeEnum ttype = modelField.getExactTtype();
        String model = modelField.getReferences();
        if (TtypeEnum.isRelationType(ttype) && StringUtils.isNotBlank(model)) {
            List<UIOption> optionsList = uiField.getOptions();
            UIOption option;
            if (CollectionUtils.isEmpty(optionsList)) {
                optionsList = new ArrayList<>();
                option = new UIOption();
                optionsList.add(option);
            } else {
                option = optionsList.get(0);
            }

            ModelDefinition referenceModelDefinition = modelDefinitionSupplier.apply(model);
            List<String> referenceLabelFields = null;
            if (null != referenceModelDefinition) {
                option.setCompiled(true);
                option.setReferences(model);
                option.setReferencesModelName(referenceModelDefinition.getName());
                String referenceModule = referenceModelDefinition.getModule();
                if (StringUtils.isNotBlank(referenceModule)) {
                    String referenceModuleName = Optional.ofNullable(PamirsSession.getContext().getModule(referenceModule))
                            .map(ModuleDefinition::getName).orElse(null);
                    option.setReferencesModuleName(referenceModuleName);
                }
                option.setReferencesType(referenceModelDefinition.getType());
                option.setReferencesPks(referenceModelDefinition.getPk());
                option.setReferencesUniques(referenceModelDefinition.getUniques());
                referenceLabelFields = referenceModelDefinition.getLabelFields();
                option.setReferencesLabelFields(referenceLabelFields);
            }

            Set<String> existFieldOptionFieldSet = new HashSet<>();
            Set<String> fields = new HashSet<>();
            List<UIWidget> fieldOptions = option.getWidgets();
            List<String> optionFields = uiField.getOptionFields();
            List<String> referenceFields = uiField.getReferenceFields();
            List<String> referencePks = option.getReferencesPks();

            if (CollectionUtils.isNotEmpty(fieldOptions)) {
                for (UIWidget fieldOption : fieldOptions) {
                    UIField uiFieldOption = (UIField) fieldOption;
                    String fieldData = uiFieldOption.getData();
                    if (StringUtils.isNotBlank(fieldData)) {
                        existFieldOptionFieldSet.add(fieldData);
                        ModelField optionModelField = modelFieldSupplier.apply(model, fieldData);
                        makeOption(optionModelField, uiView, uiFieldOption);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(optionFields)) {
                fields.addAll(optionFields);
            }

            if (CollectionUtils.isNotEmpty(referenceFields)) {
                fields.addAll(referenceFields);
            }

            if (CollectionUtils.isNotEmpty(referencePks)) {
                fields.addAll(referencePks);
            }

            if (CollectionUtils.isNotEmpty(referenceLabelFields)) {
                fields.addAll(referenceLabelFields);
            } else if (null != PamirsSession.getContext().getModelField(model, FieldConstants.NAME)) {
                fields.add(FieldConstants.NAME);
            }

            Optional.ofNullable(uiField.getReferenceFields()).ifPresent(fields::addAll);
            Map<String, UIOption> uiOptionMap = new HashMap<>();
            for (String field : fields) {
                if (FieldUtils.isConstantRelationFieldValue(field)) {
                    continue;
                }
                if (existFieldOptionFieldSet.contains(field)) {
                    continue;
                }
                // 暂时只支持一层的点(.)分隔，eg：employee.id,employee.code
                if (field.contains(CharacterConstants.SEPARATOR_DOT)) {
                    String[] fieldDataSplit = field.split("\\.");
                    field = fieldDataSplit[0];
                    String subFieldData = fieldDataSplit[1];
                    ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
                    if (null == modelFieldConfig) {
                        log.warn("Field is deleted or renamed, model:{}, field:{}", model, field);
                        continue;
                    }
                    UIField subUIField = makeSubOptionField(modelFieldConfig, subFieldData);
                    if (uiOptionMap.get(field) != null) {
                        UIOption subOption = uiOptionMap.get(field);
                        subOption.addWidget(subUIField);
                    } else {
                        UIField optionField = new UIField();
                        ModelField optionModelField = modelFieldSupplier.apply(model, field);
                        option.addWidget(makeOption(optionModelField, uiView, optionField));
                        // 补充optionField的属性
                        optionField.setModel(modelFieldConfig.getReferences());

                        //构建Options
                        UIOption subOption = makeSubOption(modelDefinitionSupplier, modelFieldConfig.getReferences());
                        optionField.setOptions(Collections.singletonList(subOption));

                        if (subUIField != null) {
                            subOption.addWidget(subUIField);
                            uiOptionMap.put(field, subOption);
                        }
                    }
                } else {
                    UIField optionField = new UIField();
                    ModelField optionModelField = modelFieldSupplier.apply(model, field);
                    option.addWidget(makeOption(optionModelField, uiView, optionField));
                }
            }

            uiField.setOptions(optionsList);
        } else if (TtypeEnum.ENUM.equals(ttype)) {
            List<UIOption> options = uiField.getOptions();
            if (CollectionUtils.isEmpty(options)) {
                uiField.setDictionary(modelField.getDictionary());
                fillDataDictionaryMetadata(uiView, modelField.getDictionary(), modelField.getOptions());
                return;
            }
            String defaultValue = uiField.getDefaultValue();
            List<DataDictionaryItem> dictionaryItems = modelField.getOptions();
            Map<String, DataDictionaryItem> dictionaryItemCache = Optional.ofNullable(dictionaryItems)
                    .map(List::stream)
                    .orElse(Stream.empty())
                    .collect(Collectors.toMap(DataDictionaryItem::getName, java.util.function.Function.identity(), (_a, _b) -> _a));
            boolean autoFillOptions = null != uiField.getAutoFillOptions() && uiField.getAutoFillOptions();
            List<UIOption> optionList = new ArrayList<>();
            for (UIOption option : options) {
                if (option.isCompiled()) {
                    continue;
                }
                String optionName = option.getName();
                if (StringUtils.isBlank(optionName)) {
                    continue;
                }
                DataDictionaryItem dictionaryItem = dictionaryItemCache.get(optionName);
                if (dictionaryItem != null) {
                    optionList.add(fillDictionaryOption(dictionaryItem, option));
                    dictionaryItemCache.remove(optionName);
                } else {
                    if (null != defaultValue && StringUtils.equals(optionName, defaultValue)) {
                        uiField.setDefaultValue(null);
                    }
                }
            }
            if (autoFillOptions) {
                for (Map.Entry<String, DataDictionaryItem> entry : dictionaryItemCache.entrySet()) {
                    DataDictionaryItem item = entry.getValue();
                    if (null == item) {
                        continue;
                    }
                    optionList.add(fillDictionaryOption(item, new UIOption()));
                }
            }

            uiField.setOptions(optionList);
        }
    }

    private static UIField makeOption(ModelField optionModelField, UIView uiView, UIField optionField) {
        if (null == optionModelField) {
            return optionField;
        }
        optionField.setCompiled(true);
        optionField.setName(optionModelField.getName());
        optionField.setData(optionModelField.getField());
        optionField.setLabel(optionModelField.getDisplayName());
        optionField.setMulti(optionModelField.getMulti());
        optionField.setTtype(optionModelField.getTtype());
        optionField.setRelatedTtype(optionModelField.getRelatedTtype());
        if (TtypeEnum.isRelatedType(optionModelField.getTtype().value())) {
            optionField.setRelated(Optional.ofNullable(optionModelField.getRelated()).orElse(optionModelField.getRelated()));
        }
        optionField.setStore(optionModelField.getStore());
        optionField.setRelationStore(optionModelField.getRelationStore());
        optionField.setRelationFields(optionModelField.getRelationFields());
        optionField.setReferenceFields(optionModelField.getReferenceFields());

        TtypeEnum ttype = optionModelField.getExactTtype();
        if (TtypeEnum.ENUM.equals(ttype)) {
            optionField.setDictionary(optionModelField.getDictionary());
            fillDataDictionaryMetadata(uiView, optionModelField.getDictionary(), optionModelField.getOptions());
        }
        return optionField;
    }

    /**
     * "dslNodeType": "option",
     * "references": "demo.PetTalent",
     * "referencesLabelFields": [],
     * "referencesModelName": "petTalent",
     * "referencesModuleName": "DemoCore",
     * "referencesPks": [],
     * "referencesType": "STORE",
     *
     * @param modelDefinitionSupplier
     * @param model
     * @return
     */
    private static UIOption makeSubOption(java.util.function.Function<String, ModelDefinition> modelDefinitionSupplier,
                                          String model) {
        UIOption option = new UIOption();
        ModelDefinition referenceModelDefinition = modelDefinitionSupplier.apply(model);
        List<String> referenceLabelFields = null;
        if (null != referenceModelDefinition) {
            option.setCompiled(true);
            option.setReferences(model);
            option.setReferencesModelName(referenceModelDefinition.getName());
            String referenceModule = referenceModelDefinition.getModule();
            if (StringUtils.isNotBlank(referenceModule)) {
                String referenceModuleName = Optional.ofNullable(PamirsSession.getContext().getModule(referenceModule))
                        .map(ModuleDefinition::getName).orElse(null);
                option.setReferencesModuleName(referenceModuleName);
            }
            option.setReferencesType(referenceModelDefinition.getType());
            option.setReferencesPks(referenceModelDefinition.getPk());
            referenceLabelFields = referenceModelDefinition.getLabelFields();
            option.setReferencesLabelFields(referenceLabelFields);
        }
        return option;
    }

    private static UIField makeSubOptionField(ModelFieldConfig modelFieldConfig, String subFieldData) {
        ModelFieldConfig subModelField = PamirsSession.getContext().getModelField(modelFieldConfig.getReferences(), subFieldData);
        if (subModelField == null || subModelField.getModelField() == null) {
            return null;
        }
        ModelField optionModelField = subModelField.getModelField();
        UIField subFieldOption = new UIField();
        subFieldOption.setCompiled(true);
        subFieldOption.setModel(modelFieldConfig.getReferences());
        subFieldOption.setName(optionModelField.getName());
        subFieldOption.setData(optionModelField.getField());
        subFieldOption.setLabel(optionModelField.getDisplayName());
        subFieldOption.setTtype(optionModelField.getTtype());
        subFieldOption.setRelatedTtype(optionModelField.getRelatedTtype());
        if (TtypeEnum.isRelatedType(optionModelField.getTtype().value())) {
            subFieldOption.setRelated(Optional.ofNullable(subFieldOption.getRelated()).orElse(subFieldOption.getRelated()));
        }
        subFieldOption.setStore(optionModelField.getStore());
        subFieldOption.setRelationStore(optionModelField.getRelationStore());
        subFieldOption.setRelationFields(optionModelField.getRelationFields());
        subFieldOption.setReferenceFields(optionModelField.getReferenceFields());
        return subFieldOption;
    }

    private static void fillDataDictionaryMetadata(UIView uiView, String dictionary, List<DataDictionaryItem> options) {
        UIMetadata metadata = uiView.getMetadata();
        if (metadata == null) {
            metadata = new UIMetadata();
            uiView.setMetadata(metadata);
        }
        List<UIDictionary> dataDictionaryList = metadata.getDictionary();
        if (dataDictionaryList == null) {
            dataDictionaryList = new ArrayList<>();
            metadata.setDictionary(dataDictionaryList);
        }
        for (UIDictionary dataDictionary : dataDictionaryList) {
            if (dictionary.equals(dataDictionary.getDictionary())) {
                return;
            }
        }
        dataDictionaryList.add(convertDictionary(dictionary, options));
    }

    public static UIDictionary convertDictionary(String dictionary, List<DataDictionaryItem> items) {
        UIDictionary dataDictionary = new UIDictionary();
        dataDictionary.setDictionary(dictionary);
        dataDictionary.setOptions(convertDictionaryToOptions(items));
        return dataDictionary;
    }

    public static List<UIOption> convertDictionaryToOptions(List<DataDictionaryItem> items) {
        List<UIOption> options = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (DataDictionaryItem item : items) {
                options.add(generatorDictionaryOption(item));
            }
        }
        return options;
    }

    private static UIOption generatorDictionaryOption(DataDictionaryItem dictionaryItem) {
        return fillDictionaryOption(dictionaryItem, new UIOption());
    }

    private static UIOption fillDictionaryOption(DataDictionaryItem item, UIOption option) {
        option.setDisplayName(Optional.ofNullable(option.getDisplayName()).orElse(item.getDisplayName()))
                .setState(Optional.ofNullable(option.getState()).orElse(item.getState()))
                .setValue(item.getValue())
                .setName(item.getName())
                .setCompiled(true);
        if (item.getSource() != null && !SystemSourceEnum.UI.equals(item.getSource())) {
            option.setHelp(StringUtils.defaultIfBlank(option.getHelp(), StringUtils.defaultIfBlank(option.getSummary(), item.getHelp())));
        }
        return option;
    }

    public static FunctionDefinition fetchServActionFunction(String namespace, String fun) {
        if (StringUtils.isBlank(fun)) {
            return null;
        }
        FunctionDefinition functionDef = fetchFunctionDefinition(namespace, fun);
        if (functionDef == null) {
            // Redis缓存情况下：获取不到数据，可能存在缓存中有null标识。清空再次获取会查询DB补全最新的数据
            // 内存缓存情况下 ：获取不到数据就是没有数据，执行清理也不会影响原有数据
            PamirsSession.getContext().disableFunctionCache(namespace, fun);
            functionDef = fetchFunctionDefinition(namespace, fun);
        }
        return functionDef;
    }

    public static FunctionDefinition fetchFunctionDefinition(String namespace, String fun) {
        if (StringUtils.isBlank(fun)) {
            return null;
        }
        Function function = PamirsSession.getContext().getFunctionAllowNull(namespace, fun);
        FunctionDefinition functionDefinition = null;
        if (null != function) {
            functionDefinition = FunctionDefinition.simplify(function.getFunctionDefinition());
        }
        return functionDefinition;
    }

}

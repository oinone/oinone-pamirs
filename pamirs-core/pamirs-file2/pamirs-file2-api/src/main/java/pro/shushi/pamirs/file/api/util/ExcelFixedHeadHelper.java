package pro.shushi.pamirs.file.api.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.file.api.builder.*;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTemplateTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.ExcelCellDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

/**
 * @author Adamancy Zhang on 2021-03-02 11:50
 */
@Slf4j
public class ExcelFixedHeadHelper {

    private final WorkbookDefinitionBuilder workbookDefinitionBuilder;

    private SheetDefinitionBuilder sheetDefinitionBuilder;

    private String model;

    private BlockDefinitionBuilder blockDefinitionBuilder;

    private HeaderDefinitionBuilder configHeaderBuilder;

    private HeaderDefinitionBuilder headerBuilder;

    private List<RowDefinitionBuilder<BlockDefinitionBuilder>> rowDefinitionBuilders;

    private int endColumnIndex;

    ExcelFixedHeadHelper(String model, String name) {
        workbookDefinitionBuilder = WorkbookDefinitionBuilder.newInstance(model, name);
    }

    public ExcelFixedHeadHelper setDisplayName(String displayName) {
        workbookDefinitionBuilder.setDisplayName(displayName);
        return this;
    }

    public ExcelFixedHeadHelper setType(ExcelTemplateTypeEnum type) {
        workbookDefinitionBuilder.setType(type);
        return this;
    }

    public ExcelFixedHeadHelper setEachImport(Boolean eachImport) {
        workbookDefinitionBuilder.setEachImport(eachImport);
        return this;
    }

    public ExcelFixedHeadHelper setMaxErrorLength(Integer maxErrorLength) {
        workbookDefinitionBuilder.setMaxErrorLength(maxErrorLength);
        return this;
    }

    public ExcelFixedHeadHelper setClearExportStyle(Boolean clearExportStyle) {
        workbookDefinitionBuilder.setClearExportStyle(clearExportStyle);
        return this;
    }

    public ExcelFixedHeadHelper setExcelMaxSupportLength(Integer excelMaxSupportLength) {
        workbookDefinitionBuilder.setExcelMaxSupportLength(excelMaxSupportLength);
        return this;
    }

    public ExcelFixedHeadHelper setCsvMaxSupportLength(Integer csvMaxSupportLength) {
        workbookDefinitionBuilder.setCsvMaxSupportLength(csvMaxSupportLength);
        return this;
    }

    public ExcelFixedHeadHelper setDomain(String domain) {
        workbookDefinitionBuilder.setDomain(domain);
        return this;
    }

    public ExcelFixedHeadHelper createSheet(String sheetName) {
        sheetDefinitionBuilder = workbookDefinitionBuilder.createSheet().setName(sheetName);
        return this;
    }

    public ExcelFixedHeadHelper createBlock(String model) {
        if (blockDefinitionBuilder != null) {
            blockDefinitionBuilder.modifyDesignRange(null, null, null, endColumnIndex);
        }
        this.model = model;
        this.endColumnIndex = -1;
        blockDefinitionBuilder = sheetDefinitionBuilder.createBlock(model, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "$A$1:$A$2");
        blockDefinitionBuilder.setPresetNumber(10);
        configHeaderBuilder = blockDefinitionBuilder.createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(true);
        headerBuilder = blockDefinitionBuilder.createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(true)));
        rowDefinitionBuilders = new ArrayList<>();
        return this;
    }

    public ExcelFixedHeadHelper createBlock(String sheetName, String model) {
        createSheet(sheetName);
        createBlock(model);
        return this;
    }

    public ExcelFixedHeadHelper addColumn(String fieldKey, String value, String... examples) {
        return addColumn(fieldKey, new ExcelCellDefinition().setValue(value), examples);
    }

    public ExcelFixedHeadHelper addColumn(String fieldKey, ExcelCellDefinition cellDefinition, String... examples) {
        if (cellDefinition.getType() == null) {
            fillCellDefinition(fieldKey, cellDefinition);
        }
        configHeaderBuilder.createCell()
                .setField(fieldKey)
                .setType(cellDefinition.getType())
                .setFormat(cellDefinition.getFormat())
                .setIsStatic(cellDefinition.getIsStatic())
                .setAutoSizeColumn(cellDefinition.getAutoSizeColumn());
        headerBuilder.createCell().setValue(cellDefinition.getValue());
        endColumnIndex++;
        int exampleSize = examples.length;
        while (rowDefinitionBuilders.size() < exampleSize) {
            rowDefinitionBuilders.add(blockDefinitionBuilder.createRow());
        }
        int rowSize = rowDefinitionBuilders.size();
        for (int i = 0; i < rowSize; i++) {
            String example = i < exampleSize ? examples[i] : CharacterConstants.SEPARATOR_EMPTY;
            RowDefinitionBuilder<BlockDefinitionBuilder> rowDefinitionBuilder = rowDefinitionBuilders.get(i);
            rowDefinitionBuilder.createCell().setValue(example);
        }
        blockDefinitionBuilder.setPresetNumber(Math.max(rowSize, 10));
        return this;
    }

    public ExcelFixedHeadHelper addUniques(String model) {
        List<Set<String>> uniqueSetList = new ArrayList<>();
        FetchUtil.consumerUniqueSet(model, (fields, columns) -> uniqueSetList.addAll(fields));
        Set<String> uniqueSet = new HashSet<>();
        for (Set<String> uniqueSetItem : uniqueSetList) {
            uniqueSet.addAll(uniqueSetItem);
        }
        return addUnique(model, uniqueSet.toArray(new String[0]));
    }

    public ExcelFixedHeadHelper addUnique(String model, String... fields) {
        if (fields.length == 0) {
            return this;
        }
        UniqueDefinitionBuilder<SheetDefinitionBuilder> uniqueDefinitionBuilder = sheetDefinitionBuilder.createUnique(model);
        for (String field : fields) {
            uniqueDefinitionBuilder.addUnique(field);
        }
        return this;
    }

    public ExcelWorkbookDefinition build() {
        blockDefinitionBuilder.modifyDesignRange(null, null, null, endColumnIndex);
        return workbookDefinitionBuilder.build();
    }

    private void fillCellDefinition(String fieldKey, ExcelCellDefinition cellDefinition) {
        if (StringUtils.isBlank(fieldKey)) {
            log.error("Invalid attribute config model: {}, key: {}, value: {}", model, fieldKey, cellDefinition.getValue());
            return;
        }
        ModelFieldConfig modelFieldConfig = finderModelFieldConfig(model, fieldKey.split("\\."), 0);
        if (modelFieldConfig == null) {
            log.error("Cannot automatically fill cell definition, please check if attribute config is correct model: {}, key: {}, value: {}", model, fieldKey, cellDefinition.getValue());
            return;
        }
        fillCellDefinition(modelFieldConfig, cellDefinition);
    }

    public static boolean fillCellDefinition(ModelFieldConfig modelFieldConfig, ExcelCellDefinition cellDefinition) {
        String fieldKey = modelFieldConfig.getField();
        ModelField modelField = modelFieldConfig.getModelField();
        TtypeEnum ttype = modelField.getTtype();
        if (TtypeEnum.isRelatedType(ttype.value())) {
            ttype = modelField.getRelatedTtype();
        }
        boolean isMulti = Boolean.TRUE.equals(modelFieldConfig.getMulti());
        final TtypeEnum finalTtype = ttype;
        ExcelValueTypeEnum excelValueType = BaseEnum.switchGet(ttype,
                BaseEnum.cases(TtypeEnum.INTEGER).to(() -> {
                    if (isMulti) {
                        cellDefinition.setFormat(ExcelHelper.generatorMultiValueFormatExpression());
                        return ExcelValueTypeEnum.OBJECT;
                    }
                    return ExcelValueTypeEnum.INTEGER;
                }),
                BaseEnum.cases(TtypeEnum.FLOAT, TtypeEnum.MONEY).to(() -> {
                    if (isMulti) {
                        cellDefinition.setFormat(ExcelHelper.generatorMultiValueFormatExpression());
                        return ExcelValueTypeEnum.OBJECT;
                    }
                    cellDefinition.setFormat(ExcelHelper.getNumberFormat(modelFieldConfig.getDecimal()));
                    return ExcelValueTypeEnum.NUMBER;
                }),
                BaseEnum.cases(TtypeEnum.BOOLEAN).to(() -> ExcelValueTypeEnum.BOOLEAN),
                BaseEnum.cases(TtypeEnum.DATETIME, TtypeEnum.DATE, TtypeEnum.TIME, TtypeEnum.YEAR).to(() -> {
                    String format = modelFieldConfig.getFormat();
                    if (StringUtils.isBlank(format)) {
                        format = BaseEnum.switchGet(finalTtype,
                                BaseEnum.cases(TtypeEnum.DATE).to(DateFormatEnum.DATE::value),
                                BaseEnum.cases(TtypeEnum.TIME).to(DateFormatEnum.TIME::value),
                                BaseEnum.cases(TtypeEnum.YEAR).to(DateFormatEnum.YEAR::value),
                                BaseEnum.defaults(DateFormatEnum.DATETIME::value));
                    }
                    cellDefinition.setFormat(format);
                    return ExcelValueTypeEnum.DATETIME;
                }),
                BaseEnum.cases(TtypeEnum.ENUM).to(() -> {
                    String dictionaryString = modelFieldConfig.getDictionary();
                    DataDictionary dictionary = PamirsSession.getContext().getDictionary(dictionaryString);
                    if (dictionary == null) {
                        log.error("Invalid data dictionary config fieldKey: {}, model: {}, dictionary: {}", fieldKey, modelFieldConfig.getModel(), dictionaryString);
                        return ExcelValueTypeEnum.STRING;
                    }
                    List<DataDictionaryItem> options = dictionary.getOptions();
                    if (CollectionUtils.isEmpty(options)) {
                        log.error("Invalid data dictionary item config fieldKey: {}, model: {}, dictionary: {}", fieldKey, modelFieldConfig.getModel(), dictionaryString);
                        return ExcelValueTypeEnum.STRING;
                    }
                    Map<String, String> enumerationMap = new LinkedHashMap<>(options.size());
                    for (DataDictionaryItem dictionaryItem : options) {
                        enumerationMap.put(dictionaryItem.getValue(), dictionaryItem.getDisplayName());
                    }
                    cellDefinition.setFormat(JSON.toJSONString(enumerationMap));
                    return ExcelValueTypeEnum.ENUMERATION;
                }),
                BaseEnum.cases(TtypeEnum.O2O, TtypeEnum.M2O).to(() -> {
                    relationFieldProcess(cellDefinition, modelField, false);
                    return ExcelValueTypeEnum.OBJECT;
                }),
                BaseEnum.cases(TtypeEnum.O2M, TtypeEnum.M2M).to(() -> {
                    relationFieldProcess(cellDefinition, modelField, true);
                    return ExcelValueTypeEnum.OBJECT;
                }),
                BaseEnum.defaults(() -> ExcelValueTypeEnum.STRING));
        cellDefinition.setType(excelValueType);
        return true;
    }

    private static ModelFieldConfig finderModelFieldConfig(String model, String[] fields, int index) {
        String field = fields[index];
        int li = field.indexOf("["),
                ri = field.indexOf("]");
        if (li != -1 && ri != -1) {
            field = field.substring(0, li);
        }
        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
        if (modelFieldConfig == null) {
            return null;
        }
        index++;
        if (fields.length == index) {
            return modelFieldConfig;
        }
        return finderModelFieldConfig(modelFieldConfig.getReferences(), fields, index);
    }

    private static void relationFieldProcess(ExcelCellDefinition cellDefinition, ModelField modelField, boolean isMulti) {
        RequestContext requestContext = PamirsSession.getContext();
        String referenceModel = modelField.getReferences();
        ModelConfig referenceModelConfig = requestContext.getModelConfig(referenceModel);
        List<String> optionLabels = Optional.ofNullable(referenceModelConfig.getModelDefinition())
                .map(ModelDefinition::getLabelFields)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(null);
        if (CollectionUtils.isEmpty(optionLabels)) {
            ModelFieldConfig labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.NAME);
            if (labelFieldConfig == null) {
                labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.CODE);
            }
            if (labelFieldConfig == null) {
                labelFieldConfig = requestContext.getModelField(referenceModel, FieldConstants.ID);
            }
            if (labelFieldConfig == null) {
                return;
            }
            optionLabels = Collections.singletonList(labelFieldConfig.getField());
        }
        String optionLabel = optionLabels.get(0);
        String formatExpression;
        if (isMulti) {
            formatExpression = ExcelHelper.generatorMultiObjectFormatExpression(referenceModel, optionLabel);
        } else {
            formatExpression = ExcelHelper.generatorSingleObjectFormatExpression(optionLabel);
        }
        cellDefinition.setFormat(formatExpression);
    }
}

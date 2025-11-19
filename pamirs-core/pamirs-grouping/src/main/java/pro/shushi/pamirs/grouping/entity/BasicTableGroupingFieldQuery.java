package pro.shushi.pamirs.grouping.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;

/**
 * 抽象表格分组字段查询基类
 *
 * @author Adamancy Zhang at 18:05 on 2025-11-18
 */
@Slf4j
class BasicTableGroupingFieldQuery {

    private static final String AS = " as ";

    protected final String field;

    protected final Object value;

    protected final String ttype;

    protected final boolean multi;

    protected final String columnFormat;

    protected final String column;

    protected final String asField;

    protected final List<String> pks;

    protected final List<String> pkColumns;

    protected final List<String> pkAsFields;

    protected final Boolean isBitDataDictionary;

    protected final Boolean isNumericDataDictionary;

    protected final Map<String, String> dataDictionaryOptions;

    protected final String references;

    protected final List<String> referencesPks;

    protected final List<String> referencesPkColumns;

    protected final List<String> referencesPkAsFields;

    protected final List<String> relationFields;

    protected final List<String> relationColumns;

    protected final List<String> relationAsFields;

    protected final List<String> referenceFields;

    protected final List<String> referenceColumns;

    protected final List<String> referenceAsFields;

    protected final String through;

    protected final List<String> throughRelationFields;

    protected final List<String> throughRelationColumns;

    protected final List<String> throughRelationAsFields;

    protected final List<String> throughReferenceFields;

    protected final List<String> throughReferenceColumns;

    protected final List<String> throughReferenceAsFields;

    BasicTableGroupingFieldQuery(String model, String field, Object value) {
        this.field = field;
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        this.columnFormat = pamirsTableInfo.getColumnFormat();

        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
        if (modelFieldConfig == null) {
            throw PamirsException.construct(CommonExpEnumerate.GROUPING_FIELD_NOT_FOUND, model, field).errThrow();
        }
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttype)) {
            ttype = modelFieldConfig.getRelatedTtype();
        }
        this.ttype = ttype;
        this.multi = Boolean.TRUE.equals(modelFieldConfig.getMulti());

        String column = modelFieldConfig.getColumn();
        if (StringUtils.isBlank(column)) {
            this.column = null;
            this.asField = null;
        } else {
            if (StringUtils.isBlank(columnFormat)) {
                this.column = column;
                this.asField = field;
            } else {
                this.column = String.format(columnFormat, column);
                this.asField = String.format(columnFormat, field);
            }
        }

        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        List<String> finalPks = modelConfig.getPk();
        List<String> finalPkColumns = null;
        List<String> finalPkAsFields = null;
        if (CollectionUtils.isNotEmpty(finalPks)) {
            FieldColumnsWrapper pkFieldsWrapper = resolveColumns(model, finalPks, columnFormat);
            if (pkFieldsWrapper != null) {
                finalPkColumns = pkFieldsWrapper.columns;
                finalPkAsFields = pkFieldsWrapper.asFields;
            }
        }
        this.pks = finalPks;
        this.pkColumns = finalPkColumns;
        this.pkAsFields = finalPkAsFields;

        Boolean finalIsBitDataDictionary = null;
        Boolean finalIsNumericDataDictionary = null;
        Map<String, String> finalDataDictionaryOptions = null;
        if (TtypeEnum.ENUM.value().equals(ttype)) {
            Map<String, String> dataDictionaryOptions = new HashMap<>();
            DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(modelFieldConfig.getDictionary());
            List<DataDictionaryItem> options = dataDictionary.getOptions();
            for (DataDictionaryItem option : options) {
                dataDictionaryOptions.put(option.getName(), option.getValue());
            }
            finalIsBitDataDictionary = Boolean.TRUE.equals(dataDictionary.getBit());
            finalIsNumericDataDictionary = TtypeEnum.isNumericType(dataDictionary.getValueType().value());
            finalDataDictionaryOptions = dataDictionaryOptions;
        }
        this.isBitDataDictionary = finalIsBitDataDictionary;
        this.isNumericDataDictionary = finalIsNumericDataDictionary;
        this.dataDictionaryOptions = finalDataDictionaryOptions;

        String finalReferences = null;
        List<String> finalReferencesPks = null;
        List<String> finalReferencesPkColumns = null;
        List<String> finalReferencesPkAsFields = null;
        List<String> finalRelationFields = null;
        List<String> finalRelationColumns = null;
        List<String> finalRelationAsFields = null;
        List<String> finalReferenceFields = null;
        List<String> finalReferenceColumns = null;
        List<String> finalReferenceAsFields = null;
        String finalThrough = null;
        List<String> finalThroughRelationFields = null;
        List<String> finalThroughRelationColumns = null;
        List<String> finalThroughRelationAsFields = null;
        List<String> finalThroughReferenceFields = null;
        List<String> finalThroughReferenceColumns = null;
        List<String> finalThroughReferenceAsFields = null;
        if (TtypeEnum.isRelationType(ttype)) {
            finalReferences = modelFieldConfig.getReferences();
            ModelConfig referenceModelConfig = PamirsSession.getContext().getSimpleModelConfig(finalReferences);
            finalReferencesPks = referenceModelConfig.getPk();
            if (CollectionUtils.isNotEmpty(finalReferencesPks)) {
                FieldColumnsWrapper pkFieldsWrapper = resolveColumns(finalReferences, finalReferencesPks, columnFormat);
                if (pkFieldsWrapper != null) {
                    finalReferencesPkColumns = pkFieldsWrapper.columns;
                    finalReferencesPkAsFields = pkFieldsWrapper.asFields;
                }
            }
            finalRelationFields = modelFieldConfig.getRelationFields();
            finalReferenceFields = modelFieldConfig.getReferenceFields();
            FieldColumnsWrapper relationFieldsWrapper = resolveColumns(model, finalRelationFields, columnFormat);
            if (relationFieldsWrapper != null) {
                finalRelationColumns = relationFieldsWrapper.columns;
                finalRelationAsFields = relationFieldsWrapper.asFields;
            }
            FieldColumnsWrapper referenceFieldsWrapper = resolveColumns(finalReferences, finalReferenceFields);
            if (referenceFieldsWrapper != null) {
                finalReferenceColumns = referenceFieldsWrapper.columns;
                finalReferenceAsFields = referenceFieldsWrapper.asFields;
            }
        }
        if (TtypeEnum.M2M.value().equals(ttype)) {
            finalThrough = modelFieldConfig.getThrough();
            finalThroughRelationFields = modelFieldConfig.getThroughRelationFields();
            finalThroughReferenceFields = modelFieldConfig.getThroughReferenceFields();
            FieldColumnsWrapper throughRelationFieldsWrapper = resolveColumns(finalThrough, finalThroughRelationFields);
            if (throughRelationFieldsWrapper != null) {
                finalThroughRelationColumns = throughRelationFieldsWrapper.columns;
                finalThroughRelationAsFields = throughRelationFieldsWrapper.asFields;
            }
            FieldColumnsWrapper throughReferenceFieldsWrapper = resolveColumns(finalThrough, finalThroughReferenceFields);
            if (throughReferenceFieldsWrapper != null) {
                finalThroughReferenceColumns = throughReferenceFieldsWrapper.columns;
                finalThroughReferenceAsFields = throughReferenceFieldsWrapper.asFields;
            }
        }
        this.references = finalReferences;
        this.referencesPks = finalReferencesPks;
        this.referencesPkColumns = finalReferencesPkColumns;
        this.referencesPkAsFields = finalReferencesPkAsFields;
        this.relationFields = finalRelationFields;
        this.relationColumns = finalRelationColumns;
        this.relationAsFields = finalRelationAsFields;
        this.referenceFields = finalReferenceFields;
        this.referenceColumns = finalReferenceColumns;
        this.referenceAsFields = finalReferenceAsFields;
        this.through = finalThrough;
        this.throughRelationFields = finalThroughRelationFields;
        this.throughRelationColumns = finalThroughRelationColumns;
        this.throughRelationAsFields = finalThroughRelationAsFields;
        this.throughReferenceFields = finalThroughReferenceFields;
        this.throughReferenceColumns = finalThroughReferenceColumns;
        this.throughReferenceAsFields = finalThroughReferenceAsFields;

        Object finalValue = null;
        if (TtypeEnum.isRelationOne(ttype)) {
            if (value != null) {
                QueryWrapper<Object> queryWrapper = Pops.query();
                queryWrapper.from(references);
                boolean isValidQueryWrapper = true;
                for (int i = 0; i < referencesPks.size(); i++) {
                    String pk = referencesPks.get(i);
                    String pkColumn = referenceColumns.get(i);
                    Object pkValue = FieldUtils.getFieldValue(value, pk);
                    if (pkValue == null) {
                        isValidQueryWrapper = false;
                        break;
                    }
                    queryWrapper.eq(pkColumn, pkValue);
                }
                if (isValidQueryWrapper) {
                    finalValue = Models.origin().queryOneByWrapper(queryWrapper);
                }
            }
        } else if (TtypeEnum.isRelationMany(ttype)) {
            if (value != null) {
                Collection<?> coll = (Collection<?>) value;
                if (!coll.isEmpty()) {
                    QueryWrapper<Object> queryWrapper = Pops.query();
                    queryWrapper.from(references);
                    List<List<Object>> inValues = new ArrayList<>(referencesPks.size());
                    boolean isValidQueryWrapper = true;
                    for (Object item : coll) {
                        for (int i = 0; i < referencesPks.size(); i++) {
                            if (inValues.size() < i + 1) {
                                inValues.add(new ArrayList<>());
                            }
                            String pk = referencesPks.get(i);
                            List<Object> inValue = inValues.get(i);
                            Object pkValue = FieldUtils.getFieldValue(item, pk);
                            if (pkValue == null) {
                                isValidQueryWrapper = false;
                                continue;
                            }
                            inValue.add(pkValue);
                        }
                    }
                    if (isValidQueryWrapper) {
                        queryWrapper.in(referencesPkColumns, inValues.toArray(new List[0]));
                        finalValue = Models.origin().queryListByWrapper(queryWrapper);
                    }
                }
            }
        } else {
            finalValue = value;
        }
        this.value = finalValue;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public String getTtype() {
        return ttype;
    }

    public boolean isMulti() {
        return multi;
    }

    public String getColumn() {
        return column;
    }

    public List<String> getPks() {
        return pks;
    }

    public List<String> getPkColumns() {
        return pkColumns;
    }

    public List<String> getPkAsFields() {
        return pkAsFields;
    }

    public boolean isBitDataDictionary() {
        return isBitDataDictionary;
    }

    public boolean isNumericDataDictionary() {
        return isNumericDataDictionary;
    }

    public String getDataDictionaryValue(String name) {
        return dataDictionaryOptions.get(name);
    }

    public String getReferences() {
        return references;
    }

    public List<String> getReferencesPks() {
        return referencesPks;
    }

    public List<String> getReferencesPkColumns() {
        return referencesPkColumns;
    }

    public List<String> getReferencesPkAsFields() {
        return referencesPkAsFields;
    }

    public List<String> getRelationFields() {
        return relationFields;
    }

    public List<String> getRelationColumns() {
        return relationColumns;
    }

    public List<String> getRelationAsFields() {
        return relationAsFields;
    }

    public List<String> getReferenceFields() {
        return referenceFields;
    }

    public List<String> getReferenceColumns() {
        return referenceColumns;
    }

    public List<String> getReferenceAsFields() {
        return referenceAsFields;
    }

    public String getThrough() {
        return through;
    }

    public List<String> getThroughRelationFields() {
        return throughRelationFields;
    }

    public List<String> getThroughRelationColumns() {
        return throughRelationColumns;
    }

    public List<String> getThroughRelationAsFields() {
        return throughRelationAsFields;
    }

    public List<String> getThroughReferenceFields() {
        return throughReferenceFields;
    }

    public List<String> getThroughReferenceColumns() {
        return throughReferenceColumns;
    }

    public List<String> getThroughReferenceAsFields() {
        return throughReferenceAsFields;
    }

    public boolean isBasicField() {
        return TtypeEnum.isBasicType(ttype);
    }

    public boolean isRelationField() {
        return TtypeEnum.isRelationType(ttype);
    }

    public boolean isRelationOneField() {
        return TtypeEnum.isRelationOne(ttype);
    }

    public boolean isRelationManyField() {
        return TtypeEnum.isRelationMany(ttype);
    }

    public boolean isEnumField() {
        return TtypeEnum.ENUM.value().equals(ttype);
    }

    public boolean isStringField() {
        return TtypeEnum.isStringType(ttype);
    }

    public boolean isNumberField() {
        return TtypeEnum.isNumericType(ttype);
    }

    public boolean isO2MField() {
        return TtypeEnum.O2M.value().equals(ttype);
    }

    public boolean isM2MField() {
        return TtypeEnum.M2M.value().equals(ttype);
    }

    /**
     * 是否支持单表查询
     */
    public boolean isSingleTableQuery() {
        if (value == null) {
            return !isRelationManyField();
        }
        if (isMulti()) {
            return isEnumField() && isBitDataDictionary();
        }
        return isBasicField() || isEnumField() || isRelationOneField();
    }

    public String getColumnAsField() {
        if (isRelationOneField()) {
            return getColumAsField(relationColumns, relationAsFields);
        }
        return column + AS + asField;
    }

    public String getColumAsField(List<String> relationColumns, List<String> relationAsFields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < relationColumns.size(); i++) {
            String column = relationColumns.get(i);
            String asField = relationAsFields.get(i);
            if (i != 0) {
                builder.append(CharacterConstants.SEPARATOR_COMMA);
            }
            builder.append(column).append(AS).append(asField);
        }
        return builder.toString();
    }

    private FieldColumnsWrapper resolveColumns(String model, List<String> relationFields) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        String columnFormat = pamirsTableInfo.getColumnFormat();
        return resolveColumns(model, relationFields, columnFormat);
    }

    private FieldColumnsWrapper resolveColumns(String model, List<String> fields, String columnFormat) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        List<String> relationColumns = new ArrayList<>();
        List<String> relationAsFields = new ArrayList<>();
        boolean isValidRelationOne = true;
        for (String relationField : fields) {
            ModelFieldConfig relationFieldConfig = PamirsSession.getContext().getModelField(model, relationField);
            if (relationFieldConfig == null) {
                throw PamirsException.construct(CommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, relationField).errThrow();
            }
            String relationColumn = relationFieldConfig.getColumn();
            if (StringUtils.isBlank(relationColumn)) {
                log.error("relation field is not store field. model: {}, field: {}", model, relationField);
                isValidRelationOne = false;
                break;
            } else {
                if (StringUtils.isBlank(columnFormat)) {
                    relationColumns.add(relationColumn);
                    relationAsFields.add(relationField);
                } else {
                    relationColumns.add(String.format(columnFormat, relationColumn));
                    relationAsFields.add(String.format(columnFormat, relationField));
                }
            }
        }
        if (isValidRelationOne) {
            return new FieldColumnsWrapper(relationColumns, relationAsFields);
        }
        return null;
    }

    private static class FieldColumnsWrapper {

        private final List<String> columns;

        private final List<String> asFields;

        private FieldColumnsWrapper(List<String> columns, List<String> asFields) {
            this.columns = columns;
            this.asFields = asFields;
        }
    }
}

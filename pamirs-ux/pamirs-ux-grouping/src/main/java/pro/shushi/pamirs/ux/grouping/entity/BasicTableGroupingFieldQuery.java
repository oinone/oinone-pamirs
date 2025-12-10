package pro.shushi.pamirs.ux.grouping.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.common.enumeration.UxCommonExpEnumerate;
import pro.shushi.pamirs.ux.common.utils.WrapperHelper;

import java.util.*;

/**
 * 抽象表格分组字段查询基类
 *
 * @author Adamancy Zhang at 18:05 on 2025-11-18
 */
@Slf4j
public class BasicTableGroupingFieldQuery {

    protected final TableGroupingModel model;

    protected final String field;

    /**
     * 是否为仅查询分组场景
     */
    protected final boolean grouping;

    protected final Object value;

    protected final String ttype;

    protected final boolean multi;

    protected final String format;

    protected final String column;

    protected final String asField;

    protected final Boolean isBitDataDictionary;

    protected final Boolean isNumericDataDictionary;

    protected final Map<String, String> dictionaryNameMapping;

    protected final Map<String, String> dictionaryValueMapping;

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

    BasicTableGroupingFieldQuery(TableGroupingModel model, String field, Object value, boolean grouping, boolean basic) {
        this.model = model;
        this.field = field;
        this.grouping = grouping;

        String modelModel = model.getModel();
        String columnFormat = model.getColumnFormat();

        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(modelModel, field);
        if (modelFieldConfig == null) {
            throw PamirsException.construct(UxCommonExpEnumerate.GROUPING_FIELD_NOT_FOUND, modelModel, field).errThrow();
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

        if (isDateField()) {
            this.format = FieldFix.fixFormat(modelFieldConfig.getModelField());
        } else {
            this.format = null;
        }

        Boolean finalIsBitDataDictionary = null;
        Boolean finalIsNumericDataDictionary = null;
        Map<String, String> finalDictionaryNameMapping = null;
        Map<String, String> finalDictionaryValueMapping = null;
        if (isEnumField()) {
            Map<String, String> dictionaryNameMapping = new HashMap<>();
            Map<String, String> dictionaryValueMapping = new HashMap<>();
            DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(modelFieldConfig.getDictionary());
            List<DataDictionaryItem> options = dataDictionary.getOptions();
            for (DataDictionaryItem option : options) {
                dictionaryNameMapping.put(option.getName(), option.getValue());
                dictionaryValueMapping.put(option.getValue(), option.getName());
            }
            finalIsBitDataDictionary = Boolean.TRUE.equals(dataDictionary.getBit());
            finalIsNumericDataDictionary = TtypeEnum.isNumericType(dataDictionary.getValueType().value());
            finalDictionaryNameMapping = dictionaryNameMapping;
            finalDictionaryValueMapping = dictionaryValueMapping;
        }
        this.isBitDataDictionary = finalIsBitDataDictionary;
        this.isNumericDataDictionary = finalIsNumericDataDictionary;
        this.dictionaryNameMapping = finalDictionaryNameMapping;
        this.dictionaryValueMapping = finalDictionaryValueMapping;

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
        if (isRelationField()) {
            finalReferences = modelFieldConfig.getReferences();
            ModelConfig referenceModelConfig = PamirsSession.getContext().getSimpleModelConfig(finalReferences);
            finalReferencesPks = referenceModelConfig.getPk();
            if (CollectionUtils.isNotEmpty(finalReferencesPks)) {
                FieldColumnsWrapper pkFieldsWrapper = FieldColumnsWrapper.resolveColumns(finalReferences, finalReferencesPks, columnFormat);
                if (pkFieldsWrapper != null) {
                    finalReferencesPkColumns = pkFieldsWrapper.getColumns();
                    finalReferencesPkAsFields = pkFieldsWrapper.getAsFields();
                }
            }
            finalRelationFields = modelFieldConfig.getRelationFields();
            finalReferenceFields = modelFieldConfig.getReferenceFields();
            FieldColumnsWrapper relationFieldsWrapper = FieldColumnsWrapper.resolveColumns(modelModel, finalRelationFields, columnFormat);
            if (relationFieldsWrapper != null) {
                finalRelationColumns = relationFieldsWrapper.getColumns();
                finalRelationAsFields = relationFieldsWrapper.getAsFields();
            }
            FieldColumnsWrapper referenceFieldsWrapper = FieldColumnsWrapper.resolveColumns(finalReferences, finalReferenceFields);
            if (referenceFieldsWrapper != null) {
                finalReferenceColumns = referenceFieldsWrapper.getColumns();
                finalReferenceAsFields = referenceFieldsWrapper.getAsFields();
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

        if (basic) {
            this.value = null;
            this.through = null;
            this.throughRelationFields = null;
            this.throughRelationColumns = null;
            this.throughRelationAsFields = null;
            this.throughReferenceFields = null;
            this.throughReferenceColumns = null;
            this.throughReferenceAsFields = null;
            return;
        }

        String finalThrough = null;
        List<String> finalThroughRelationFields = null;
        List<String> finalThroughRelationColumns = null;
        List<String> finalThroughRelationAsFields = null;
        List<String> finalThroughReferenceFields = null;
        List<String> finalThroughReferenceColumns = null;
        List<String> finalThroughReferenceAsFields = null;
        if (isM2MField()) {
            finalThrough = modelFieldConfig.getThrough();
            finalThroughRelationFields = modelFieldConfig.getThroughRelationFields();
            finalThroughReferenceFields = modelFieldConfig.getThroughReferenceFields();
            FieldColumnsWrapper throughRelationFieldsWrapper = FieldColumnsWrapper.resolveColumns(finalThrough, finalThroughRelationFields);
            if (throughRelationFieldsWrapper != null) {
                finalThroughRelationColumns = throughRelationFieldsWrapper.getColumns();
                finalThroughRelationAsFields = throughRelationFieldsWrapper.getAsFields();
            }
            FieldColumnsWrapper throughReferenceFieldsWrapper = FieldColumnsWrapper.resolveColumns(finalThrough, finalThroughReferenceFields);
            if (throughReferenceFieldsWrapper != null) {
                finalThroughReferenceColumns = throughReferenceFieldsWrapper.getColumns();
                finalThroughReferenceAsFields = throughReferenceFieldsWrapper.getAsFields();
            }
        }
        this.through = finalThrough;
        this.throughRelationFields = finalThroughRelationFields;
        this.throughRelationColumns = finalThroughRelationColumns;
        this.throughRelationAsFields = finalThroughRelationAsFields;
        this.throughReferenceFields = finalThroughReferenceFields;
        this.throughReferenceColumns = finalThroughReferenceColumns;
        this.throughReferenceAsFields = finalThroughReferenceAsFields;

        if (grouping) {
            this.value = null;
        } else {
            Object finalValue = null;
            if (isRelationOneField()) {
                if (value != null) {
                    QueryWrapper<Object> queryWrapper = Pops.query();
                    queryWrapper.from(references);
                    boolean isValidQueryWrapper = true;
                    for (int i = 0; i < referencesPks.size(); i++) {
                        String pk = referencesPks.get(i);
                        String pkColumn = referencesPkColumns.get(i);
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
            } else if (isRelationManyField()) {
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
    }

    public TableGroupingModel getModel() {
        return model;
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

    public String getFormat() {
        return format;
    }

    public String getColumn() {
        return column;
    }

    public String getAsField() {
        return asField;
    }

    public boolean isBitDataDictionary() {
        return isBitDataDictionary;
    }

    public boolean isNumericDataDictionary() {
        return isNumericDataDictionary;
    }

    public String getDataDictionaryName(String value) {
        return dictionaryValueMapping.get(value);
    }

    public String getDataDictionaryValue(String name) {
        return dictionaryNameMapping.get(name);
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
        return TtypeEnum.isBasicType(ttype) || TtypeEnum.MONEY.value().equals(ttype);
    }

    public boolean isRelationField() {
        return TtypeEnum.isRelationType(ttype);
    }

    public boolean isSupportRelationQuery() {
        return CollectionUtils.isNotEmpty(relationFields) && CollectionUtils.isNotEmpty(referenceFields);
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
        return TtypeEnum.isNumericType(ttype) || TtypeEnum.MONEY.value().equals(ttype);
    }

    public boolean isDateField() {
        return TtypeEnum.isDateType(ttype);
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
        boolean isSupportSingleTableQuery = StringUtils.isNotBlank(column) || (isRelationOneField() && CollectionUtils.isNotEmpty(relationColumns));
        if (!isSupportSingleTableQuery) {
            return false;
        }
        if (!grouping && value == null) {
            return !isRelationManyField();
        }
        if (isMulti()) {
            return isEnumField() && isBitDataDictionary();
        }
        return isBasicField() || isEnumField() || isRelationOneField();
    }

    /**
     * 是否支持单表分组
     */
    public boolean isSingleTableGrouping() {
        boolean isSupportSingleTableQuery = StringUtils.isNotBlank(column) || (isRelationOneField() && CollectionUtils.isNotEmpty(relationColumns));
        if (!isSupportSingleTableQuery) {
            return false;
        }
        if (isMulti()) {
            return !isRelationManyField();
        }
        return isBasicField() || isEnumField() || isRelationOneField();
    }

    public String getColumnAsField() {
        if (isRelationOneField()) {
            return WrapperHelper.getColumAsField(relationColumns, relationAsFields);
        }
        return WrapperHelper.getColumAsField(column, asField);
    }

    public <T> void withNullWhere(QueryWrapper<T> queryWrapper) {
        if (isStringField()) {
            queryWrapper.and(w -> w.isNull(column).or().eq(column, CharacterConstants.SEPARATOR_EMPTY));
        } else if (isRelationOneField()) {
            for (String relationColumn : relationColumns) {
                queryWrapper.isNull(relationColumn);
            }
        } else {
            queryWrapper.isNull(column);
        }
    }

    public <T> void withNotNullWhere(QueryWrapper<T> queryWrapper) {
        if (isStringField()) {
            queryWrapper.isNotNull(column).ne(column, CharacterConstants.SEPARATOR_EMPTY);
        } else if (isRelationOneField()) {
            for (String relationColumn : relationColumns) {
                queryWrapper.isNotNull(relationColumn);
            }
        } else {
            queryWrapper.isNotNull(column);
        }
    }
}
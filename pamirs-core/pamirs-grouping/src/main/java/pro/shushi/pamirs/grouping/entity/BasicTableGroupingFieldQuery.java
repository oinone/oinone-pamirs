package pro.shushi.pamirs.grouping.entity;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象表格分组字段查询基类
 *
 * @author Adamancy Zhang at 18:05 on 2025-11-18
 */
@Slf4j
class BasicTableGroupingFieldQuery {

    private static final String AS = " as ";

    protected final String field;

    protected final String ttype;

    protected final boolean multi;

    protected final String columnFormat;

    protected final String column;

    protected final String asField;

    protected final Boolean isBitDataDictionary;

    protected final Boolean isNumericDataDictionary;

    protected final Map<String, String> dataDictionaryOptions;

    protected final String references;

    protected final List<String> relationFields;

    protected final List<String> relationColumns;

    protected final List<String> relationAsFields;

    BasicTableGroupingFieldQuery(String model, String field) {
        this.field = field;
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        this.columnFormat = pamirsTableInfo.getColumnFormat();

        ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
        if (modelFieldConfig == null) {
            throw PamirsException.construct(CommonExpEnumerate.GROUPING_FIELD_NOT_FOUND).errThrow();
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

        if (TtypeEnum.ENUM.value().equals(ttype)) {
            Map<String, String> dataDictionaryOptions = new HashMap<>();
            DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(modelFieldConfig.getDictionary());
            List<DataDictionaryItem> options = dataDictionary.getOptions();
            for (DataDictionaryItem option : options) {
                dataDictionaryOptions.put(option.getName(), option.getValue());
            }
            this.isBitDataDictionary = Boolean.TRUE.equals(dataDictionary.getBit());
            this.isNumericDataDictionary = TtypeEnum.isNumericType(dataDictionary.getValueType().value());
            this.dataDictionaryOptions = dataDictionaryOptions;
        } else {
            this.isBitDataDictionary = null;
            this.isNumericDataDictionary = null;
            this.dataDictionaryOptions = null;
        }

        if (TtypeEnum.isRelationOne(ttype)) {
            List<String> relationFields = modelFieldConfig.getRelationFields();
            List<String> relationColumns = new ArrayList<>();
            List<String> relationAsFields = new ArrayList<>();
            boolean isValidRelationOne = true;
            for (String relationField : relationFields) {
                String relationColumn = Configs.wrap(PamirsSession.getContext().getModelField(model, relationField)).getColumn();
                if (StringUtils.isBlank(relationColumn)) {
                    log.error("relation field is not store field. model: {}, field: {}, relationField: {}", model, field, relationField);
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
                this.references = modelFieldConfig.getReferences();
                this.relationFields = relationFields;
                this.relationColumns = relationColumns;
                this.relationAsFields = relationAsFields;
            } else {
                this.references = null;
                this.relationFields = null;
                this.relationColumns = null;
                this.relationAsFields = null;
            }
        } else {
            this.references = null;
            this.relationFields = null;
            this.relationColumns = null;
            this.relationAsFields = null;
        }
    }

    public String getField() {
        return field;
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

    public boolean isBasicField() {
        return TtypeEnum.isBasicType(ttype);
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

    public String getColumnAsField() {
        return column + AS + asField;
    }
}

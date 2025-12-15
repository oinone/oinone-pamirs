package pro.shushi.pamirs.ux.grouping.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.common.enumeration.UxCommonExpEnumerate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class FieldColumnsWrapper {

    private final List<String> columns;

    private final List<String> asFields;

    private FieldColumnsWrapper(List<String> columns, List<String> asFields) {
        this.columns = columns;
        this.asFields = asFields;
    }

    protected List<String> getColumns() {
        return columns;
    }

    protected List<String> getAsFields() {
        return asFields;
    }

    protected static FieldColumnsWrapper resolveColumns(String model, List<String> relationFields) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        String columnFormat = pamirsTableInfo.getColumnFormat();
        return resolveColumns(model, relationFields, columnFormat);
    }

    protected static FieldColumnsWrapper resolveColumns(String model, List<String> fields, String columnFormat) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        List<String> relationColumns = new ArrayList<>();
        List<String> relationAsFields = new ArrayList<>();
        boolean isValidRelationOne = true;
        for (String relationField : fields) {
            if (FieldUtils.isConstantRelationFieldValue(relationField)) {
                continue;
            }
            ModelFieldConfig relationFieldConfig = PamirsSession.getContext().getModelField(model, relationField);
            if (relationFieldConfig == null) {
                throw PamirsException.construct(UxCommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, relationField).errThrow();
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
}
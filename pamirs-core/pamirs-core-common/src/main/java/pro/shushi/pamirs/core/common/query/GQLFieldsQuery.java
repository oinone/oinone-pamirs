package pro.shushi.pamirs.core.common.query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.core.common.tmodel.CommonGQLFields;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 根据 GQL 字段查询
 *
 * @author Adamancy Zhang at 10:59 on 2025-11-19
 */
public class GQLFieldsQuery {

    private Map<String, Function<String, String>> columnFormatCache = new HashMap<>();

    private final Map<String, List<String>> columnsMap;

    private final Map<String, List<String>> relationFieldsMap;

    private final Map<String, List<String>> relatedFieldsMap;

    public GQLFieldsQuery(Map<String, List<String>> columnsMap, Map<String, List<String>> relationFieldsMap, Map<String, List<String>> relatedFieldsMap) {
        this.columnsMap = columnsMap;
        this.relationFieldsMap = relationFieldsMap;
        this.relatedFieldsMap = relatedFieldsMap;
    }

    public List<String> getColumns(String key) {
        return columnsMap.get(key);
    }

    public List<String> getRelationFields(String key) {
        return relationFieldsMap.get(key);
    }

    public List<String> getRelatedFields(String key) {
        return relatedFieldsMap.get(key);
    }

    private Function<String, String> getColumnFormatConvert(String model) {
        return columnFormatCache.computeIfAbsent(model, k -> {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
            String columnFormat = pamirsTableInfo.getColumnFormat();
            Function<String, String> convertColumn;
            if (StringUtils.isBlank(columnFormat)) {
                convertColumn = (v) -> v;
            } else {
                convertColumn = (v) -> String.format(columnFormat, v);
            }
            return convertColumn;
        });
    }

    public static GQLFieldsQuery resolveGQLFields(String model, CommonGQLFields gqlFields) {
        GQLFieldsQuery query = new GQLFieldsQuery(new HashMap<>(), new HashMap<>(), new HashMap<>());
        List<String> normalFields = gqlFields.getFields();
        if (CollectionUtils.isNotEmpty(normalFields)) {
            resolveNormalFields(query, model, normalFields, model);
        }
        List<CommonGQLFields> relationFields = gqlFields.getRelationFields();
        if (CollectionUtils.isNotEmpty(relationFields)) {
            resolveRelationFields(query, model, relationFields, model);
        }
        query.columnFormatCache = null;
        return query;
    }

    private static void resolveNormalFields(GQLFieldsQuery query, String model, List<String> fields, String key) {
        Function<String, String> columnFormatConvert = query.getColumnFormatConvert(model);
        List<String> columns = new ArrayList<>();
        List<String> relatedFields = new ArrayList<>();
        for (String field : fields) {
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(CommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, field).errThrow();
            }
            if (TtypeEnum.isRelatedType(modelFieldConfig.getTtype())) {
                relatedFields.add(field);
            }
            String lname = modelFieldConfig.getLname();
            String column = modelFieldConfig.getColumn();
            if (StringUtils.isNotBlank(column)) {
                columns.add(WrapperHelper.getColumAsField(columnFormatConvert.apply(column), columnFormatConvert.apply(lname)));
            }
        }
        query.columnsMap.put(key, columns);
        query.relatedFieldsMap.put(key, relatedFields);
    }

    private static void resolveRelationFields(GQLFieldsQuery query, String model, List<CommonGQLFields> gqlFieldsList, String key) {
        List<String> relationFields = new ArrayList<>();
        for (CommonGQLFields gqlFields : gqlFieldsList) {
            String field = gqlFields.getField();
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
            if (modelFieldConfig == null) {
                throw PamirsException.construct(CommonExpEnumerate.MODEL_FIELD_NOT_FOUND, model, field).errThrow();
            }
            relationFields.add(field);
            String references = modelFieldConfig.getReferences();
            String nextKey = key + CharacterConstants.SEPARATOR_OCTOTHORPE + field;
            List<String> normalFields = gqlFields.getFields();
            if (CollectionUtils.isNotEmpty(normalFields)) {
                resolveNormalFields(query, references, normalFields, nextKey);
            }
            List<CommonGQLFields> nextRelationFields = gqlFields.getRelationFields();
            if (CollectionUtils.isNotEmpty(nextRelationFields)) {
                resolveRelationFields(query, references, nextRelationFields, nextKey);
            }
        }
        query.relationFieldsMap.put(key, relationFields);
    }
}

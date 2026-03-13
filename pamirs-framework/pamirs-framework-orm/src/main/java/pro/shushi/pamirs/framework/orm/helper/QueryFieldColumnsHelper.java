package pro.shushi.pamirs.framework.orm.helper;

import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 关联字段转换为Columns工具类
 * <p>
 * 2024/08/14
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Data
public class QueryFieldColumnsHelper {

    public static List<String> fetchQueryFieldColumns(ModelFieldConfig relRefField, String queryModel, List<String> queryFields) {
        List<String> queryColumns = new ArrayList<>();
        int i = 0;
        for (String queryField : queryFields) {
            ModelFieldConfig queryFieldConfig = null;
            if (FieldUtils.isConstantRelationFieldValue(queryField)) {
                queryFieldConfig = fetchQueryFieldConfig(relRefField, queryModel, relRefField.getThroughReferenceFields().get(i));
            } else {
                queryFieldConfig = fetchQueryFieldConfig(relRefField, queryModel, queryField);
            }
            String column = ModelFieldConfigWrapper.wrap(queryFieldConfig).getSqlSelect();
            queryColumns.add(column);
            i++;
        }
        return queryColumns;
    }

    public static ModelFieldConfig fetchQueryFieldConfig(ModelFieldConfig modelFieldConfig, String queryModel, String queryField) {
        ModelFieldConfig relationFieldConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelField(queryModel, queryField);
        if (null == relationFieldConfig) {
            log.error("Query ModelField is null. Params: model:{}, Field:{}", queryModel, queryField);
            // 如果reference模型所在模块不依赖当前模块，则需要手动配置reference模型中的关联字段配置
            throw PamirsException.construct(OrmExpEnumerate.BASE_DISTRIBUTION_FIELD_IS_NOT_EXISTS_ERROR)
                    .appendMsg("model:" + modelFieldConfig.getModel() + ",relation:" + modelFieldConfig.getField() + ",ref:" + queryField)
                    .errThrow();
        }
        return relationFieldConfig;
    }

}

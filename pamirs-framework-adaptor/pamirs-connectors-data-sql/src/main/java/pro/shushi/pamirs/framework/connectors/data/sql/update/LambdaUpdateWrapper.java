package pro.shushi.pamirs.framework.connectors.data.sql.update;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractLambdaWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.SharedString;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lambda 更新封装
 */
@SuppressWarnings("serial")
public class LambdaUpdateWrapper<T> extends AbstractLambdaWrapper<T, LambdaUpdateWrapper<T>>
        implements Update<LambdaUpdateWrapper<T>, Getter<T, ?>> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaUpdate()
     */
    public LambdaUpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this((T) null);
    }

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaUpdate(entity)
     */
    public LambdaUpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaUpdate(entityClass)
     */
    public LambdaUpdateWrapper(Class<T> entityClass) {
        this((T) null);
        this.from(entityClass);
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaUpdate(...)
     */
    LambdaUpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq,
                        Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                        SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
    }

    @Override
    public LambdaUpdateWrapper<T> set(boolean condition, Getter<T, ?> column, Object val) {
        if (!condition)
            return typedThis;
        ModelFieldConfig modelFieldConfig = fetchModelFieldConfig(column);
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.M2O.value().equals(ttype) || TtypeEnum.O2O.value().equals(ttype)) {
            if (val instanceof D) {
                RequestContext requestContext = PamirsSession.getContext();
                String model = modelFieldConfig.getModel();
                List<String> relationFields = modelFieldConfig.getRelationFields();
                List<String> referenceFields = modelFieldConfig.getReferenceFields();
                int i = 0;
                for (String relationField : relationFields)
                    sqlSet.add(String.format("%s=%s", Configs.wrap(requestContext.getModelField(model, relationField)).getSqlSelect(Boolean.TRUE),
                            FieldUtils.getReferenceFieldValue(val, modelFieldConfig.getReferences(), referenceFields.get(i++))));
            } else {
                throw new RuntimeException(String.format("约定检查，多对一和一对一字段必须传入对象 [field %s]", modelFieldConfig.getField()));
            }
        } else
            sqlSet.add(String.format("%s=%s", columnToString(column), formatSql("{0}", val)));
        return typedThis;
    }

    @Override
    public LambdaUpdateWrapper<T> setSql(boolean condition, String sql) {
        if (condition && StringUtils.isNotBlank(sql)) {
            sqlSet.add(sql);
        }
        return typedThis;
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(CharacterConstants.SEPARATOR_COMMA, sqlSet);
    }

    /**
     * 返回一个支持 generic 的 wrapper
     */
    public LambdaUpdateWrapper<DataMap> generic(DataMap entity) {
        return new LambdaUpdateWrapper<>(genericCheck(entity), sqlSet, paramNameSeq, paramNameValuePairs, expression,
                lastSql, sqlComment);
    }

    public LambdaUpdateWrapper<DataMap> generic(String model, DataMap entity) {
        return generic(entity.setModel(model));
    }

    @Override
    protected LambdaUpdateWrapper<T> instance() {
        return new LambdaUpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs, new MergeSegments(),
                SharedString.emptyString(), SharedString.emptyString());
    }
}

package pro.shushi.pamirs.framework.connectors.data.sql.query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractLambdaWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.SharedString;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static pro.shushi.pamirs.framework.connectors.data.sql.enmu.SqlExpEnumerate.BASE_LAMBDA_QUERY_WRAPPER_SELECT_ERROR;

/**
 * Lambda 语法使用 Wrapper
 */
@SuppressWarnings("serial")
public class LambdaQueryWrapper<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapper<T>>
        implements Query<LambdaQueryWrapper<T>, T, Getter<T, ?>> {

    /**
     * 查询字段
     */
    private SharedString sqlSelect = new SharedString();

    /**
     * 非分页查询批次数量
     */
    protected int batchSize;

    /**
     * 是否需要排序,默认排序
     */
    protected Boolean sortable;

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaQuery()
     */
    public LambdaQueryWrapper() {
        this((T) null);
    }

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaQuery(entity)
     */
    public LambdaQueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    /**
     * 不建议直接 new 该实例，使用 Pops.lambdaQuery(entityClass)
     */
    public LambdaQueryWrapper(Class<T> entityClass) {
        this((T) null);
        this.from(entityClass);
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
     */
    LambdaQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                       Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                       SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.entityClass = entityClass;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
    }

    /**
     * SELECT 部分 SQL 设置
     *
     * @param columns 查询字段
     */
    @SafeVarargs
    @Override
    public final LambdaQueryWrapper<T> select(Getter<T, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            ModelFieldConfig modelFieldConfig;
            List<String> columnList = new ArrayList<>();
            String ttype;
            for (Getter<T, ?> column : columns) {
                modelFieldConfig = fetchModelFieldConfig(column);
                ttype = modelFieldConfig.getTtype();
                RequestContext requestContext = PamirsSession.getContext();
                String model = modelFieldConfig.getModel();
                boolean usingRelationFields = false;
                if (TtypeEnum.M2O.value().equals(ttype) || TtypeEnum.O2O.value().equals(ttype)) {
                    if (!Boolean.TRUE.equals(modelFieldConfig.getStore()) && CollectionUtils.isNotEmpty(modelFieldConfig.getRelationFields())) {
                        usingRelationFields = true;
                    }
                }
                if (usingRelationFields) {
                    List<String> relationFields = modelFieldConfig.getRelationFields();
                    for (String relationField : relationFields) {
                        columnList.add(Configs.wrap(requestContext.getModelField(model, relationField)).getSqlSelect(Boolean.FALSE));
                    }
                } else {
                    columnList.add(Configs.wrap(requestContext.getModelField(model, modelFieldConfig.getField())).getSqlSelect(Boolean.FALSE));
                }
            }
            this.sqlSelect.setStringValue(String.join(CharacterConstants.SEPARATOR_COMMA, columnList));
        }
        return typedThis;
    }

    @Override
    public LambdaQueryWrapper<T> select(Predicate<ModelFieldConfig> predicate) {
        return select(entityClass, predicate);
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     * <p>例1: 只要 java 字段名以 "test" 开头的             -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * <p>例2: 只要 java 字段属性是 CharSequence 类型的     -> select(TableFieldInfo::isCharSequence)</p>
     * <p>例3: 只要 java 字段没有填充策略的                 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)</p>
     * <p>例4: 要全部字段                                   -> select(i -&gt; true)</p>
     * <p>例5: 只要主键字段                                 -> select(i -&gt; false)</p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    @Override
    public LambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<ModelFieldConfig> predicate) {
        this.entityClass = entityClass;
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(getModel())).orElse(null);
        if (null == modelConfig) {
            throw PamirsException.construct(BASE_LAMBDA_QUERY_WRAPPER_SELECT_ERROR).errThrow();
        }
        this.sqlSelect.setStringValue(ModelConfigWrapper.wrap(modelConfig).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    /**
     * 返回一个支持 generic 的 wrapper
     */
    public LambdaQueryWrapper<DataMap> generic(DataMap entity) {
        return new QueryWrapper<DataMap>(genericCheck(entity), sqlSelect, paramNameSeq, paramNameValuePairs, expression,
                lastSql, sqlComment).lambda();
    }

    public LambdaQueryWrapper<DataMap> generic(String model, DataMap entity) {
        return generic(entity.setModel(model));
    }

    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect 不向下传递</p>
     */
    @Override
    protected LambdaQueryWrapper<T> instance() {
        return new LambdaQueryWrapper<>(entity, entityClass, null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString());
    }

    public int getBatchSize() {
        return batchSize;
    }

    public LambdaQueryWrapper<T> setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return typedThis;
    }

    public Boolean getSortable() {
        return sortable;
    }

    public LambdaQueryWrapper<T> setSortable(Boolean sortable) {
        this.sortable = sortable;
        return typedThis;
    }

}

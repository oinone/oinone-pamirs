package pro.shushi.pamirs.framework.connectors.data.sql.query;

import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.SharedString;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelConfigWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static pro.shushi.pamirs.framework.connectors.data.sql.enmu.SqlExpEnumerate.BASE_QUERY_WRAPPER_SELECT_ERROR;

/**
 * Entity 对象封装操作类
 */
@SuppressWarnings("serial")
public class QueryWrapper<T> extends AbstractWrapper<T, String, QueryWrapper<T>>
        implements Query<QueryWrapper<T>, T, String> {

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

    public QueryWrapper() {
        this(null);
    }

    public QueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public QueryWrapper(T entity, String... columns) {
        super.setEntity(entity);
        super.initNeed();
        this.select(columns);
    }

    /**
     * 非对外公开的构造方法,只用于生产嵌套 sql
     *
     * @param entityClass 本不应该需要的
     */
    private QueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
                         Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                         SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.entityClass = entityClass;
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
    }

    /**
     * 非对外公开的构造方法,只用于生产泛化wrapper
     */
    QueryWrapper(DataMap entity, SharedString sqlSelect, AtomicInteger paramNameSeq,
                 Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                 SharedString lastSql, SharedString sqlComment) {
        super.setEntity(entity);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
    }

    @Override
    public QueryWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(String.join(CharacterConstants.SEPARATOR_COMMA, columns));
        }
        return typedThis;
    }

    @Override
    public QueryWrapper<T> select(Predicate<ModelFieldConfig> predicate) {
        return select(entityClass, predicate);
    }

    @Override
    public QueryWrapper<T> select(Class<T> entityClass, Predicate<ModelFieldConfig> predicate) {
        this.entityClass = entityClass;
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelConfig(getModel())).orElse(null);
        if (null == modelConfig) {
            throw PamirsException.construct(BASE_QUERY_WRAPPER_SELECT_ERROR).errThrow();
        }
        this.sqlSelect.setStringValue(ModelConfigWrapper.wrap(modelConfig).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public LambdaQueryWrapper<T> lambda() {
        String rsql = this.getRsql();
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>(entity, entityClass, sqlSelect, paramNameSeq, paramNameValuePairs, expression,
                lastSql, sqlComment)
                .from(this.getModel());
        wrapper.setRsql(rsql);
        return wrapper;
    }

    /**
     * 返回一个支持 generic 的 wrapper
     */
    public QueryWrapper<DataMap> generic(DataMap entity) {
        return new QueryWrapper<>(genericCheck(entity), sqlSelect, paramNameSeq, paramNameValuePairs, expression,
                lastSql, sqlComment);
    }

    public QueryWrapper<DataMap> generic(String model, DataMap entity) {
        return generic(entity.setModel(model));
    }

    /**
     * 用于生成嵌套 sql
     * <p>
     * 故 sqlSelect 不向下传递
     * </p>
     */
    @Override
    protected QueryWrapper<T> instance() {
        return new QueryWrapper<>(entity, entityClass, paramNameSeq, paramNameValuePairs, new MergeSegments(),
                SharedString.emptyString(), SharedString.emptyString());
    }

    public int getBatchSize() {
        return batchSize;
    }

    public QueryWrapper<T> setBatchSize(Integer batchSize) {
        if (null == batchSize) {
            batchSize = 0;
        }
        this.batchSize = batchSize;
        return typedThis;
    }

    public Boolean getSortable() {
        return sortable;
    }

    public QueryWrapper<T> setSortable(Boolean sortable) {
        this.sortable = sortable;
        return typedThis;
    }

}

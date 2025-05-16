package pro.shushi.pamirs.framework.connectors.data.sql.update;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.SharedString;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Update 条件封装
 */
@SuppressWarnings("serial")
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>>
        implements Update<UpdateWrapper<T>, String> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    public UpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this(null);
    }

    public UpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    private UpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq,
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

    /**
     * 非对外公开的构造方法,只用于生产泛化wrapper
     */
    UpdateWrapper(DataMap entity, List<String> sqlSet, AtomicInteger paramNameSeq,
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
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return null;
        }
        return String.join(CharacterConstants.SEPARATOR_COMMA, sqlSet);
    }

    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object val) {
        if (condition) {
            sqlSet.add(String.format("%s=%s", column, formatSql("{0}", val)));
        }
        return typedThis;
    }

    @Override
    public UpdateWrapper<T> setSql(boolean condition, String sql) {
        if (condition && StringUtils.isNotBlank(sql)) {
            sqlSet.add(sql);
        }
        return typedThis;
    }

    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     */
    public LambdaUpdateWrapper<T> lambda() {
        String rsql = this.getRsql();
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs, expression, lastSql, sqlComment)
                .from(this.getModel());
        wrapper.setRsql(rsql);
        return wrapper;
    }

    /**
     * 返回一个支持 generic 的 wrapper
     */
    public UpdateWrapper<DataMap> generic(DataMap entity) {
        return new UpdateWrapper<>(genericCheck(entity), sqlSet, paramNameSeq, paramNameValuePairs, expression,
                lastSql, sqlComment);
    }

    public UpdateWrapper<DataMap> generic(String model, DataMap entity) {
        return generic(entity.setModel(model));
    }

    @Override
    protected UpdateWrapper<T> instance() {
        return new UpdateWrapper<>(entity, sqlSet, paramNameSeq, paramNameValuePairs, new MergeSegments(),
                SharedString.emptyString(), SharedString.emptyString());
    }
}

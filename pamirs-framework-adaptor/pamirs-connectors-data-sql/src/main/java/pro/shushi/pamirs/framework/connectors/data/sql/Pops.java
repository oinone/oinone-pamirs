package pro.shushi.pamirs.framework.connectors.data.sql;

import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.framework.connectors.data.sql.update.LambdaUpdateWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.update.UpdateWrapper;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Wrapper 条件构造
 */
public final class Pops {

    /**
     * 空的 EmptyWrapper
     */
    private static final QueryWrapper<?> emptyWrapper = new EmptyWrapper<>();

    private Pops() {
        // ignore
    }

    /**
     * 获取 QueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> WrapperBuilder<T> f(IWrapper<?> queryWrapper) {
        if (queryWrapper instanceof QueryWrapper) {
            return new WrapperBuilder<T>((QueryWrapper<T>) queryWrapper);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static class WrapperBuilder<T> {

        private final QueryWrapper<T> queryWrapper;

        private <K, V> String f(Getter<K, V> getter) {

            if (null != queryWrapper.getModel()) {
                return PamirsSession.getContext()
                        .getModelFieldByFieldName(queryWrapper.getModel(), Pops.f(getter))
                        .getColumn();
            } else {
                throw new UnsupportedOperationException();
            }
        }

        public WrapperBuilder(QueryWrapper<T> queryWrapper) {
            this.queryWrapper = queryWrapper;
        }

        public <K, V> WrapperBuilder<T> eq(Getter<K, V> getter, V v) {
            this.queryWrapper.eq(f(getter), v);
            return this;
        }

        public WrapperBuilder<T> from(String modelModel) {
            this.queryWrapper.from(modelModel);
            return this;
        }

        public <K, V> WrapperBuilder<T> ne(Getter<K, V> getter, V v) {
            this.queryWrapper.ne(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> in(Getter<K, V> getter, Collection<V> v) {
            this.queryWrapper.in(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> notIn(Getter<K, V> getter, Collection<V> v) {
            this.queryWrapper.notIn(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> isNull(Getter<K, V> getter) {
            this.queryWrapper.isNull(f(getter));
            return this;
        }

        public <K, V> WrapperBuilder<T> isNotNull(Getter<K, V> getter) {
            this.queryWrapper.isNotNull(f(getter));
            return this;
        }

        public <K, V> WrapperBuilder<T> ge(Getter<K, V> getter, V v) {
            this.queryWrapper.ge(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> gt(Getter<K, V> getter, V v) {
            this.queryWrapper.gt(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> le(Getter<K, V> getter, V v) {
            this.queryWrapper.le(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> lt(Getter<K, V> getter, V v) {
            this.queryWrapper.lt(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> like(Getter<K, V> getter, V v) {
            this.queryWrapper.like(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> likeLeft(Getter<K, V> getter, V v) {
            this.queryWrapper.likeLeft(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> likeRight(Getter<K, V> getter, V v) {
            this.queryWrapper.likeRight(f(getter), v);
            return this;
        }

        public <K, V> WrapperBuilder<T> between(Getter<K, V> getter, Object v0, Object v1) {
            this.queryWrapper.between(f(getter), v0, v1);
            return this;
        }

        public QueryWrapper<T> get() {
            return this.queryWrapper;
        }
    }

    /**
     * 获取 QueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param entityClass 实体类class
     * @param <T>         实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        return new LambdaQueryWrapper<>(entityClass);
    }

    /**
     * 获取 UpdateWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return UpdateWrapper&lt;T&gt;
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 UpdateWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return UpdateWrapper&lt;T&gt;
     */
    public static <T> UpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaUpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param entityClass 实体类class
     * @param <T>         实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(Class<T> entityClass) {
        return new LambdaUpdateWrapper<>(entityClass);
    }

    /**
     * 获取 EmptyWrapper&lt;T&gt;
     *
     * @param <T> 任意泛型
     * @return EmptyWrapper&lt;T&gt;
     * @see EmptyWrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) emptyWrapper;
    }

    /**
     * 一个空的QueryWrapper子类该类不包含任何条件
     *
     * @param <T>
     * @see QueryWrapper
     */
    private static class EmptyWrapper<T> extends QueryWrapper<T> {

        private static final long serialVersionUID = -2515957613998092272L;

        @Override
        public T getEntity() {
            return null;
        }

        @Override
        public EmptyWrapper<T> setEntity(T entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSqlSelect() {
            return null;
        }

        @Override
        public MergeSegments getExpression() {
            return null;
        }

        @Override
        public boolean isEmptyOfWhere() {
            return true;
        }

        @Override
        public boolean isEmptyOfNormal() {
            return true;
        }

        @Override
        public boolean nonEmptyOfEntity() {
            return !isEmptyOfEntity();
        }

        @Override
        public boolean isEmptyOfEntity() {
            return true;
        }

        @Override
        protected void initEntityClass() {
        }

        @Override
        protected Class<T> getCheckEntityClass() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EmptyWrapper<T> last(boolean condition, String lastSql) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected EmptyWrapper<T> doIt(boolean condition, ISqlSegment... sqlSegments) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSqlSegment() {
            return null;
        }

        @Override
        public Map<String, Object> getParamNameValuePairs() {
            return Collections.emptyMap();
        }

        @Override
        protected ISqlSegment columnsToString(String... columns) {
            return null;
        }

        @Override
        protected ISqlSegment columnToString(String column) {
            return null;
        }

        @Override
        protected EmptyWrapper<T> instance() {
            throw new UnsupportedOperationException();
        }
    }

    public static <T, R> String f(Getter<T, R> fn) {
        return LambdaUtil.fetchFieldName(fn);
    }

}

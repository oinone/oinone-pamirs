package pro.shushi.pamirs.grouping.query.grouping;

import pro.shushi.pamirs.core.common.query.GQLFieldsQuery;
import pro.shushi.pamirs.core.common.tmodel.CommonGQLFields;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 表格分组查询上下文
 *
 * @author Adamancy Zhang at 16:49 on 2025-11-14
 */
public class TableGroupingQueryContext<T> {

    private final String model;

    private final List<TableGroupingFieldQuery> queryList;

    private final GQLFieldsQuery gqlFieldsQuery;

    private Pagination<T> pagination;

    private String authSql;

    private Long totalElements;

    public TableGroupingQueryContext(List<TableGroupingFieldQuery> queryList) {
        this(queryList, null);
    }

    public TableGroupingQueryContext(List<TableGroupingFieldQuery> queryList, CommonGQLFields gqlFields) {
        this.model = queryList.get(0).getModel();
        this.queryList = queryList;
        if (gqlFields == null) {
            this.gqlFieldsQuery = null;
        } else {
            this.gqlFieldsQuery = GQLFieldsQuery.resolveGQLFields(this.model, gqlFields);
        }
    }

    public String getModel() {
        return model;
    }

    public List<TableGroupingFieldQuery> getQueryList() {
        return queryList;
    }

    public GQLFieldsQuery getGqlFieldsQuery() {
        return gqlFieldsQuery;
    }

    public Pagination<T> getPagination() {
        return pagination;
    }

    public void setPagination(Pagination<T> pagination) {
        this.pagination = pagination;
    }

    public QueryWrapper<T> generatorQueryWrapper() {
        return generatorQueryWrapper(queryList.get(0));
    }

    public QueryWrapper<T> generatorQueryWrapper(TableGroupingFieldQuery query) {
        QueryWrapper<T> queryWrapper = query.generatorQueryWrapper();
        if (authSql != null) {
            queryWrapper.apply(authSql);
        }
        return queryWrapper;
    }

    public QueryWrapper<T> generatorQueryWrapperWithOrderBy() {
        return generatorQueryWrapperWithOrderBy(queryList.get(0));
    }

    public QueryWrapper<T> generatorQueryWrapperWithOrderBy(TableGroupingFieldQuery query) {
        QueryWrapper<T> queryWrapper = query.generatorQueryWrapperWithOrderBy();
        if (authSql != null) {
            queryWrapper.apply(authSql);
        }
        return queryWrapper;
    }

    public QueryWrapper<T> generatorQueryWrapperWithGroupBy() {
        return generatorQueryWrapperWithGroupBy(queryList.get(0));
    }

    public QueryWrapper<T> generatorQueryWrapperWithGroupBy(TableGroupingFieldQuery query) {
        QueryWrapper<T> queryWrapper = query.generatorQueryWrapperWithGroupBy();
        if (authSql != null) {
            queryWrapper.apply(authSql);
        }
        return queryWrapper;
    }

    public String getAuthSql() {
        return authSql;
    }

    public void setAuthSql(String authSql) {
        this.authSql = authSql;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}

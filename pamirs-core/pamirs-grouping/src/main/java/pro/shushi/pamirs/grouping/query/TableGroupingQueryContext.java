package pro.shushi.pamirs.grouping.query;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.query.GQLFieldsQuery;
import pro.shushi.pamirs.core.common.tmodel.CommonConditionWrapper;
import pro.shushi.pamirs.core.common.tmodel.CommonGQLFields;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingModel;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 表格分组查询上下文
 *
 * @author Adamancy Zhang at 16:49 on 2025-11-14
 */
public class TableGroupingQueryContext<T> {

    private final CommonConditionWrapper wrapper;

    private final TableGroupingModel model;

    private final List<TableGroupingFieldQuery> queryList;

    private final GQLFieldsQuery gqlFieldsQuery;

    private Pagination<T> pagination;

    private String authSql;

    private Long totalElements;

    private TableGroupingQueryStrategy queryStrategy;

    public TableGroupingQueryContext(List<TableGroupingFieldQuery> queryList, CommonConditionWrapper wrapper) {
        this(queryList, wrapper, null);
    }

    public TableGroupingQueryContext(List<TableGroupingFieldQuery> queryList, CommonConditionWrapper wrapper, CommonGQLFields gqlFields) {
        this.wrapper = wrapper;
        this.model = queryList.get(0).getModel();
        this.queryList = queryList;
        if (gqlFields == null) {
            this.gqlFieldsQuery = null;
        } else {
            this.gqlFieldsQuery = GQLFieldsQuery.resolveGQLFields(wrapper.getModel(), gqlFields);
        }
    }

    public String getModel() {
        return model.getModel();
    }

    public TableGroupingModel getGroupingModel() {
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
        String model = getModel();
        QueryWrapper<T> queryWrapper = Pops.query();
        queryWrapper.setQueryData(wrapper.getQueryData());
        queryWrapper.from(model);
        String rsql = wrapper.getRsql();
        if (StringUtils.isNotBlank(rsql)) {
            queryWrapper.apply(FetchUtil.rsqlToSql(model, rsql));
        }
        if (authSql != null) {
            queryWrapper.apply(authSql);
        }
        queryWrapper.setBatchSize(-1);
        return queryWrapper;
    }

    public QueryWrapper<T> generatorQueryWrapperWithOrderBy() {
        QueryWrapper<T> queryWrapper = generatorQueryWrapper();
        wrapper.withOrderBy(queryWrapper);
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

    public TableGroupingQueryStrategy getQueryStrategy() {
        return queryStrategy;
    }

    public void setQueryStrategy(TableGroupingQueryStrategy queryStrategy) {
        this.queryStrategy = queryStrategy;
    }
}

package pro.shushi.pamirs.ux.grouping.api;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.ux.grouping.configure.GroupingConfigure;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.model.GroupingData;
import pro.shushi.pamirs.ux.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.ux.grouping.model.TableGroupingWrapper;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingCommonQueryApi;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryStrategy;
import pro.shushi.pamirs.ux.grouping.query.data.TableGroupingDataQueryApi;
import pro.shushi.pamirs.ux.grouping.query.grouping.TableGroupingQueryApi;
import pro.shushi.pamirs.ux.grouping.query.statistic.TableGroupingStatisticQueryApi;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingDataHelper;

import java.util.List;

/**
 * 默认表格分组API
 *
 * @author Gesi at 16:42 on 2025/9/1
 */
@SuppressWarnings("unchecked")
@Base
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultTableGroupingApi {

    private static final String QUERY_GROUPING_PAGE_FUN = "queryGroupingPage";

    private static final String QUERY_GROUPING_DATA_BY_WRAPPER_FUN = "queryGroupingDataByWrapper";

    private static final String QUERY_GROUPING_STATISTIC_FUN = "queryGroupingStatistic";

    @Function.Advanced(displayName = "查询分组信息", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public <T> TableGroupingResult queryGroupingPage(Pagination<T> page, TableGroupingWrapper wrapper) {
        page = validatePage(page);
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, true);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper(), wrapper.getGqlFields());
        String model = context.getModel();
        context.setPagination(page);
        context.setAuthSql(AuthApi.get().getDataFilter(model, QUERY_GROUPING_PAGE_FUN));
        Long totalElements = Models.origin().count(context.generatorQueryWrapper());
        if (totalElements == null) {
            totalElements = 0L;
        }
        boolean isFetchAll = isFetchAll(model, page, totalElements);
        TableGroupingResult result = new TableGroupingResult();
        context.setTotalElements(totalElements);
        TableGroupingQueryStrategy queryStrategy = new TableGroupingQueryStrategy();
        queryStrategy.setFetchAll(isFetchAll);
        queryStrategy.setRelationManyShowNull(isRelationManyShowNull(model));
        context.setQueryStrategy(queryStrategy);
        fetchApi(TableGroupingQueryApi.class, context).queryGroupingPage(context, result);
        result.setExpandedAll(computeExpandedAll(model, result.getGroups()));
        return result;
    }

    @Function.Advanced(displayName = "查询分组数据", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public <T> List<T> queryGroupingDataByWrapper(TableGroupingWrapper wrapper) {
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, false);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper());
        context.setAuthSql(AuthApi.get().getDataFilter(context.getModel(), QUERY_GROUPING_DATA_BY_WRAPPER_FUN));
        return fetchApi(TableGroupingDataQueryApi.class, context).queryGroupingDataByWrapper(context);
    }

    @Function.Advanced(displayName = "查询分组统计", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public <T> String queryGroupingStatistic(TableGroupingWrapper wrapper) {
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, false);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper());
        context.setAuthSql(AuthApi.get().getDataFilter(context.getModel(), QUERY_GROUPING_STATISTIC_FUN));
        return fetchApi(TableGroupingStatisticQueryApi.class, context).queryGroupingStatistic(context);
    }

    private <T> Pagination<T> validatePage(Pagination<T> page) {
        if (page == null) {
            page = new Pagination<>(1, 15);
        }
        if (page.getCurrentPage() == null) {
            page.setCurrentPage(1);
        }
        if (page.getSize() == null) {
            page.setSize(15L);
        }
        return page;
    }

    private boolean isFetchAll(String model, Pagination<?> pagination, Long totalElements) {
        long size = pagination.getSize();
        if (size < 0) {
            return true;
        }
        int fullQueryCount = GroupingConfigure.getFullQueryCount(model);
        if (fullQueryCount == 0) {
            return false;
        }
        if (fullQueryCount < 0) {
            return true;
        }
        return totalElements.compareTo((long) fullQueryCount) <= 0;
    }

    private boolean isRelationManyShowNull(String model) {
//        return GroupingConfigure.isRelationManyShowNull(model);
        return true;
    }

    private boolean computeExpandedAll(String model, List<GroupingData> groups) {
        int expendedAllCount = GroupingConfigure.getExpendedAllCount(model);
        if (expendedAllCount == 0) {
            return false;
        }
        if (expendedAllCount < 0) {
            return true;
        }
        return statisticGroupingDataSize(groups, expendedAllCount) != 0;
    }

    private int statisticGroupingDataSize(List<GroupingData> groups, int expendedAllCount) {
        int total = 0;
        for (GroupingData groupingData : groups) {
            if (groupingData.getIsLeaf()) {
                Integer dataSize = groupingData.getDataSize();
                if (dataSize == null) {
                    return 0;
                }
                total += dataSize;
            } else {
                List<GroupingData> nextGroups = groupingData.getGroups();
                if (CollectionUtils.isNotEmpty(nextGroups)) {
                    total += statisticGroupingDataSize(nextGroups, expendedAllCount);
                    if (total > expendedAllCount) {
                        return 0;
                    }
                }
            }
        }
        if (total > expendedAllCount) {
            return 0;
        }
        return total;
    }

    private <T, API extends TableGroupingCommonQueryApi<T>> API fetchApi(Class<API> clazz, TableGroupingQueryContext<T> context) {
        for (API api : BeanDefinitionUtils.getBeansOfTypeByOrdered(clazz)) {
            if (api.match(context)) {
                return api;
            }
        }
        throw new NullPointerException();
    }
}

package pro.shushi.pamirs.grouping.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.executor.DataPermissionExecutor;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.model.TableGroupingWrapper;
import pro.shushi.pamirs.grouping.query.TableGroupingCommonQueryApi;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.query.data.TableGroupingDataQueryApi;
import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryApi;
import pro.shushi.pamirs.grouping.query.statistic.TableGroupingStatisticQueryApi;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

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
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, true);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper(), wrapper.getGqlFields());
        context.setPagination(page);
        context.setAuthSql(DataPermissionExecutor.getFilter(context.getModel(), QUERY_GROUPING_PAGE_FUN));
        Long totalElements = Models.origin().count(context.generatorQueryWrapper());
        TableGroupingResult result = new TableGroupingResult();
        result.setTotalDataCount(totalElements);
        context.setTotalElements(totalElements);
        fetchApi(TableGroupingQueryApi.class, context).queryGroupingPage(context, result);
        return result;
    }

    @Function.Advanced(displayName = "查询分组数据", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public <T> List<T> queryGroupingDataByWrapper(TableGroupingWrapper wrapper) {
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, false);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper());
        context.setAuthSql(DataPermissionExecutor.getFilter(context.getModel(), QUERY_GROUPING_DATA_BY_WRAPPER_FUN));
        return fetchApi(TableGroupingDataQueryApi.class, context).queryGroupingDataByWrapper(context);
    }

    @Function.Advanced(displayName = "查询分组统计", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public <T> String queryGroupingStatistic(TableGroupingWrapper wrapper) {
        List<TableGroupingFieldQuery> queryList = TableGroupingDataHelper.prepareGroupingFields(wrapper, false);
        TableGroupingQueryContext<T> context = new TableGroupingQueryContext<>(queryList, wrapper.getQueryWrapper());
        context.setAuthSql(DataPermissionExecutor.getFilter(context.getModel(), QUERY_GROUPING_STATISTIC_FUN));
        return fetchApi(TableGroupingStatisticQueryApi.class, context).queryGroupingStatistic(context);
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

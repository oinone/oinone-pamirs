package pro.shushi.pamirs.grouping.query.statistic;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.BasicTableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingModel;
import pro.shushi.pamirs.grouping.entity.TableGroupingStatisticFieldQuery;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApiFactory;
import pro.shushi.pamirs.grouping.statistic.StatisticField;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 万能的表格数据统计查询查询
 *
 * @author Adamancy Zhang at 09:41 on 2025-11-18
 */
@Order(999)
@Component
public class TableGroupingStatisticUniversalQuery<T> implements TableGroupingStatisticQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return true;
    }

    @Override
    public String queryGroupingStatistic(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        return fetchGroupingStatisticData(context.getGroupingModel(), queryList, context.generatorQueryWrapper());
    }

    private String fetchGroupingStatisticData(TableGroupingModel model, List<TableGroupingFieldQuery> queryList, QueryWrapper<T> queryWrapper) {
        List<TableGroupingFieldQuery> memoryQueryList = new ArrayList<>();
        Set<String> columns = new LinkedHashSet<>();
        List<String> pkColumns = model.getPkColumns();
        if (CollectionUtils.isNotEmpty(pkColumns)) {
            columns.addAll(WrapperHelper.getColumAsFields(pkColumns, model.getPkAsFields()));
        }
        for (TableGroupingFieldQuery query : queryList) {
            appendColumns(columns, query);
            if (query.isSingleTableQuery()) {
                query.withWhere(queryWrapper);
            } else {
                memoryQueryList.add(query);
            }
        }
        TableGroupingStatisticFieldQuery statisticQuery = queryList.get(queryList.size() - 1).getStatisticQuery();
        appendColumns(columns, statisticQuery);
        queryWrapper.select(columns.toArray(new String[0]));

        String statisticMethod = statisticQuery.getStatisticMethod();
        StatisticField statisticField = new StatisticField(
                model.getModel(),
                statisticQuery.getField(),
                statisticQuery.getTtype(),
                statisticQuery.getInvalidStatisticValue()
        );
        StatisticApi<T> api = StatisticApiFactory.getApi(statisticMethod, statisticField);
        if (api == null) {
            throw PamirsException.construct(CommonExpEnumerate.STATISTIC_API_NOT_FOUND, statisticMethod).errThrow();
        }
        List<String> needQueryRelationFields = new ArrayList<>();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isSupportRelationQuery()) {
                needQueryRelationFields.add(query.getField());
            }
        }
        if (statisticQuery.isSupportRelationQuery()) {
            needQueryRelationFields.add(statisticQuery.getField());
        }
        if (needQueryRelationFields.isEmpty() && memoryQueryList.isEmpty()) {
            FetchUtil.fetchDataList(model.getModel(), queryWrapper, api::compute);
        } else if (memoryQueryList.isEmpty()) {
            FetchUtil.fetchDataList(model.getModel(), queryWrapper, (list) -> {
                for (String field : needQueryRelationFields) {
                    list = Models.origin().listFieldQuery(list, field);
                }
                api.compute(list);
            });
        } else {
            FetchUtil.fetchDataList(model.getModel(), queryWrapper, (list) -> {
                for (String field : needQueryRelationFields) {
                    list = Models.origin().listFieldQuery(list, field);
                }
                TableGroupingHelper.filter(list, memoryQueryList);
                api.compute(list);
            });
        }
        return api.getResult();
    }

    private void appendColumns(Set<String> columns, BasicTableGroupingFieldQuery query) {
        String column = query.getColumn();
        if (StringUtils.isNotBlank(column)) {
            columns.add(WrapperHelper.getColumAsField(column, query.getAsField()));
        }
        List<String> relationColumns = query.getRelationColumns();
        if (CollectionUtils.isNotEmpty(relationColumns)) {
            columns.addAll(WrapperHelper.getColumAsFields(relationColumns, query.getRelationAsFields()));
        }
    }
}

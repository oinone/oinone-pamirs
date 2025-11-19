package pro.shushi.pamirs.grouping.query.statistic;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryContext;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单表数据库的表格数据统计查询
 *
 * @author Adamancy Zhang at 12:17 on 2025-11-17
 */
@Order(0)
@Component
public class TableGroupingStatisticSingleColumnQuery<T> implements TableGroupingStatisticQueryApi<T> {

    private static final List<GroupStatisticMethodEnum> DB_STATISTIC_METHODS = Lists.newArrayList(
            GroupStatisticMethodEnum.NULL,
            GroupStatisticMethodEnum.NOT_NULL,
            GroupStatisticMethodEnum.COUNT,
            GroupStatisticMethodEnum.EARLIEST_TIME,
            GroupStatisticMethodEnum.MIN,
            GroupStatisticMethodEnum.LATEST_TIME,
            GroupStatisticMethodEnum.MAX,
//            GroupStatisticMethodEnum.TIME_RANGE_DAY,
//            GroupStatisticMethodEnum.TIME_RANGE_MONTH,
//            GroupStatisticMethodEnum.TIME_RANGE_YEAR,
            GroupStatisticMethodEnum.SUM,
            GroupStatisticMethodEnum.AVERAGE
    );

    @Resource
    private GenericMapper genericMapper;

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        for (TableGroupingFieldQuery query : queryList) {
            if (!query.isSingleTableQuery()) {
                return false;
            }
        }
        return DB_STATISTIC_METHODS.contains(queryList.get(queryList.size() - 1).getStatisticMethod());
    }

    @Override
    public String queryGroupingStatistic(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        for (TableGroupingFieldQuery query : queryList) {
            queryWrapper.groupBy(query.getColumn());
            query.withWhere(queryWrapper);
        }
        TableGroupingFieldQuery lastQuery = queryList.get(queryList.size() - 1);
        lastQuery.withStatistic(queryWrapper);
        if (GroupStatisticMethodEnum.NULL.equals(lastQuery.getStatisticMethod())
                || GroupStatisticMethodEnum.NOT_NULL.equals(lastQuery.getStatisticMethod())) {
            return statisticNullValue(context, queryWrapper, lastQuery);
        }
        DataMap data = genericMapper.selectOne(queryWrapper.generic(context.getModel(), new DataMap()));
        if (data == null) {
            return null;
        }
        return String.valueOf(FieldUtils.getFieldValue(data, lastQuery.getStatisticField()));
    }

    private String statisticNullValue(TableGroupingQueryContext<T> context, QueryWrapper<T> queryWrapper, TableGroupingFieldQuery lastQuery) {
        String statisticField = lastQuery.getStatisticField();
        List<DataMap> list = genericMapper.selectList(queryWrapper.generic(context.getModel(), new DataMap()));
        long statisticValue = 0L;
        for (DataMap data : list) {
            if (data == null) {
                continue;
            }
            Object value = FieldUtils.getFieldValue(data, statisticField);
            if (value != null) {
                statisticValue += Long.parseLong(String.valueOf(value));
            }
        }
        return String.valueOf(statisticValue);
    }
}

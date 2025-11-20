package pro.shushi.pamirs.grouping.query.statistic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.core.common.NumberHelper;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingStatisticFieldQuery;
import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.statistic.StatisticHelper;
import pro.shushi.pamirs.grouping.utils.TableGroupingStatisticHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.util.FieldUtils;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 单表数据库的表格数据统计查询
 *
 * @author Adamancy Zhang at 12:17 on 2025-11-17
 */
@Slf4j
@Order(0)
@Component
public class TableGroupingStatisticDatabaseQuery<T> implements TableGroupingStatisticQueryApi<T> {

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
        return queryList.get(queryList.size() - 1).getStatisticQuery().isSupportDatabaseStatistic();
    }

    @Override
    public String queryGroupingStatistic(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        int lastIndex = queryList.size() - 1;
        for (int i = 0; i < lastIndex; i++) {
            TableGroupingFieldQuery query = queryList.get(i);
            queryWrapper.groupBy(query.getColumn());
            query.withWhere(queryWrapper);
        }
        TableGroupingFieldQuery lastQuery = queryList.get(lastIndex);
        queryWrapper.groupBy(lastQuery.getColumn());

        String model = context.getModel();
        TableGroupingStatisticFieldQuery statisticQuery = lastQuery.getStatisticQuery();
        GroupStatisticMethodEnum statisticMethod = statisticQuery.getInternalStatisticMethod();
        switch (statisticMethod) {
            case COUNT:
                lastQuery.withWhere(queryWrapper);
                return String.valueOf(count(model, queryWrapper));
            case NULL:
                lastQuery.withWhere(queryWrapper);
                statisticQuery.withNullWhere(queryWrapper);
                return String.valueOf(count(model, queryWrapper));
            case NOT_NULL:
                lastQuery.withWhere(queryWrapper);
                statisticQuery.withNotNullWhere(queryWrapper);
                return String.valueOf(count(model, queryWrapper));
            case NULL_PERCENT: {
                lastQuery.withWhere(queryWrapper);
                long total = count(model, queryWrapper);
                statisticQuery.withNullWhere(queryWrapper);
                long nullCount = count(model, queryWrapper);
                return StatisticHelper.computePercent(nullCount, total);
            }
            case NOT_NULL_PERCENT: {
                lastQuery.withWhere(queryWrapper);
                long total = count(model, queryWrapper);
                statisticQuery.withNotNullWhere(queryWrapper);
                long notNullCount = count(model, queryWrapper);
                return StatisticHelper.computePercent(notNullCount, total);
            }
            default:
                return defaultStatisticValue(context, lastQuery, queryWrapper);
        }
    }

    private String defaultStatisticValue(TableGroupingQueryContext<T> context, TableGroupingFieldQuery query, QueryWrapper<T> queryWrapper) {
        TableGroupingStatisticFieldQuery statisticQuery = query.getStatisticQuery();
        query.withStatistic(queryWrapper);
        DataMap data = genericMapper.selectOne(queryWrapper.generic(context.getModel(), new DataMap()));
        if (data == null) {
            return statisticQuery.getInvalidStatisticValue();
        }
        if (statisticQuery.isDifferenceStatisticValue()) {
            return diff(statisticQuery, data);
        }
        Object statisticValue = FieldUtils.getFieldValue(data, statisticQuery.getField());
        if (statisticValue == null) {
            return statisticQuery.getInvalidStatisticValue();
        }
        return String.valueOf(statisticValue);
    }

    private String diff(TableGroupingStatisticFieldQuery query, DataMap data) {
        Object minObjectValue = FieldUtils.getFieldValue(data, query.getAsMinField());
        Object maxObjectValue = FieldUtils.getFieldValue(data, query.getAsMaxField());
        GroupStatisticMethodEnum statisticMethod = query.getInternalStatisticMethod();
        if (query.isNumberField()) {
            return NumberHelper.valueOf(maxObjectValue)
                    .subtract(NumberHelper.valueOf(minObjectValue))
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();
        } else if (query.isDateField()) {
            if (minObjectValue == null || maxObjectValue == null) {
                return query.getInvalidStatisticValue();
            }
            String format = query.getFormat();
            Date minDate = dateParse((String) minObjectValue, format);
            Date maxDate = dateParse((String) maxObjectValue, format);
            if (minDate == null || maxDate == null) {
                return query.getInvalidStatisticValue();
            }
            switch (query.getInternalStatisticMethod()) {
                case TIME_RANGE_DAY:
                    return String.valueOf(TableGroupingStatisticHelper.timeRangeDay(maxDate, minDate));
                case TIME_RANGE_MONTH:
                    return String.valueOf(TableGroupingStatisticHelper.timeRangeMonth(maxDate, minDate));
                case TIME_RANGE_YEAR:
                    return String.valueOf(TableGroupingStatisticHelper.timeRangeYear(maxDate, minDate));
                default:
                    throw new UnsupportedOperationException("Invalid statistic method. statistic: " + statisticMethod);
            }
        } else {
            throw new UnsupportedOperationException("Invalid statistic method. statistic: " + statisticMethod);
        }
    }

    private Date dateParse(String value, String format) {
        try {
            return DateHelper.parse(value, format);
        } catch (ParseException e) {
            log.error("Invalid datetime value and format. value: {}, format: {}", value, format, e);
            return null;
        }
    }

    private long count(String model, QueryWrapper<T> queryWrapper) {
        Long value = genericMapper.selectCount(queryWrapper.generic(model, new DataMap()));
        if (value == null) {
            return 0L;
        }
        return value;
    }
}

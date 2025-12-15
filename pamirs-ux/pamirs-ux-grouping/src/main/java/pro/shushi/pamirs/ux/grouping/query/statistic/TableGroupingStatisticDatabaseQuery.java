package pro.shushi.pamirs.ux.grouping.query.statistic;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.common.utils.NumberHelper;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingStatisticFieldQuery;
import pro.shushi.pamirs.ux.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.ux.grouping.statistic.StatisticHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        TableGroupingStatisticFieldQuery statisticQuery = queryList.get(queryList.size() - 1).getStatisticQuery();
        return statisticQuery.isSingleTableQuery() && statisticQuery.isSupportDatabaseStatistic();
    }

    @Override
    public String queryGroupingStatistic(TableGroupingQueryContext<T> context) {
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapper();
        int lastIndex = queryList.size() - 1;
        for (int i = 0; i < lastIndex; i++) {
            TableGroupingFieldQuery query = queryList.get(i);
            query.withGroupBy(queryWrapper);
            query.withWhere(queryWrapper);
        }
        TableGroupingFieldQuery lastQuery = queryList.get(lastIndex);
        lastQuery.withGroupBy(queryWrapper);

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
        if (statisticQuery.isNumberField()) {
            BigDecimal numberValue = NumberHelper.valueOfNullable(statisticValue);
            if (numberValue == null) {
                return statisticQuery.getInvalidStatisticValue();
            }
            if (statisticQuery.isFloatField()) {
                return numberValue.setScale(statisticQuery.getDecimal(), RoundingMode.HALF_UP).toPlainString();
            }
            return numberValue.toPlainString();
        } else if (statisticQuery.isDateField()) {
            String format = statisticQuery.getFormat();
            Date date = dateParse(statisticQuery, statisticValue, format);
            if (date == null) {
                return statisticQuery.getInvalidStatisticValue();
            }
            return new SimpleDateFormat(format).format(date);
        }
        return String.valueOf(statisticValue);
    }

    private String diff(TableGroupingStatisticFieldQuery query, DataMap data) {
        Object minObjectValue = FieldUtils.getFieldValue(data, query.getMinField());
        Object maxObjectValue = FieldUtils.getFieldValue(data, query.getMaxField());
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
            Date minDate = dateParse(query, minObjectValue, format);
            Date maxDate = dateParse(query, maxObjectValue, format);
            if (minDate == null || maxDate == null) {
                return query.getInvalidStatisticValue();
            }
            switch (query.getInternalStatisticMethod()) {
                case TIME_RANGE_DAY:
                    return String.valueOf(StatisticHelper.timeRangeDay(maxDate, minDate));
                case TIME_RANGE_MONTH:
                    return String.valueOf(StatisticHelper.timeRangeMonth(maxDate, minDate));
                case TIME_RANGE_YEAR:
                    return String.valueOf(StatisticHelper.timeRangeYear(maxDate, minDate));
                default:
                    throw new UnsupportedOperationException("Invalid statistic method. statistic: " + statisticMethod);
            }
        } else {
            throw new UnsupportedOperationException("Invalid statistic method. statistic: " + statisticMethod);
        }
    }

    private Date dateParse(TableGroupingStatisticFieldQuery query, Object value, String format) {
        String stringValue = null;
        if (value instanceof String) {
            stringValue = (String) value;
        } else if (value instanceof Date) {
            Long dateLong = castDateLong(query, value);
            if (dateLong != null) {
                return new Date(dateLong);
            }
        }
        try {
            return new SimpleDateFormat(format).parse(stringValue);
        } catch (ParseException e) {
            log.error("Invalid datetime value and format. value: {}, format: {}", value, format, e);
            return null;
        }
    }

    private Long castDateLong(TableGroupingStatisticFieldQuery query, Object param) {
        Long dateLong = null;
        if (param instanceof Timestamp) {
            dateLong = ((Timestamp) param).getTime();
        } else if (param instanceof java.sql.Date) {
            dateLong = ((java.sql.Date) param).getTime();
        } else if (param instanceof java.sql.Time) {
            dateLong = ((java.sql.Time) param).getTime();
        } else if (param instanceof Long || param instanceof Integer) {
            if (TtypeEnum.YEAR.value().equals(query.getTtype()) && ((long) param) < 10000L) {
                return LocalDateTime.of((int) param, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            dateLong = (long) param;
        }
        return dateLong;
    }

    private long count(String model, QueryWrapper<T> queryWrapper) {
        Long value = genericMapper.selectCount(queryWrapper.generic(model, new DataMap()));
        if (value == null) {
            return 0L;
        }
        return value;
    }
}

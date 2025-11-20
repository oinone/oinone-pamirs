package pro.shushi.pamirs.grouping.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;

import java.util.List;

/**
 * 表格分组统计字段查询
 *
 * @author Adamancy Zhang at 17:53 on 2025-11-18
 */
public class TableGroupingStatisticFieldQuery extends BasicTableGroupingFieldQuery {

    private static final List<GroupStatisticMethodEnum> DB_STATISTIC_METHODS = Lists.newArrayList(
            GroupStatisticMethodEnum.COUNT,
            GroupStatisticMethodEnum.NULL,
            GroupStatisticMethodEnum.NOT_NULL,
            GroupStatisticMethodEnum.NULL_PERCENT,
            GroupStatisticMethodEnum.NOT_NULL_PERCENT,
            GroupStatisticMethodEnum.MIN,
            GroupStatisticMethodEnum.MAX,
            GroupStatisticMethodEnum.EARLIEST_TIME,
            GroupStatisticMethodEnum.LATEST_TIME,
            GroupStatisticMethodEnum.TIME_RANGE_DAY,
            GroupStatisticMethodEnum.TIME_RANGE_MONTH,
            GroupStatisticMethodEnum.TIME_RANGE_YEAR,
            GroupStatisticMethodEnum.SUM,
            GroupStatisticMethodEnum.AVERAGE
    );

    private static final String MIN_FIELD_SUFFIX = "_MIN";

    private static final String MAX_FIELD_SUFFIX = "_MAX";

    private final GroupStatisticMethodEnum statisticMethod;

    private final String invalidStatisticValue;

    private final String asMinField;

    private final String asMaxField;

    protected TableGroupingStatisticFieldQuery(TableGroupingModel model, String field, GroupStatisticMethodEnum statisticMethod) {
        super(model, field, null, false, true);
        this.statisticMethod = statisticMethod;

        String columnFormat = model.getColumnFormat();

        String finalAsMinField;
        String finalAsMaxField;
        if (StringUtils.isBlank(columnFormat)) {
            finalAsMinField = field + MIN_FIELD_SUFFIX;
            finalAsMaxField = field + MAX_FIELD_SUFFIX;
        } else {
            finalAsMinField = String.format(columnFormat, field + MIN_FIELD_SUFFIX);
            finalAsMaxField = String.format(columnFormat, field + MAX_FIELD_SUFFIX);
        }
        this.asMinField = finalAsMinField;
        this.asMaxField = finalAsMaxField;
        if (isNumberField()) {
            this.invalidStatisticValue = "0";
        } else if (isDateField()) {
            this.invalidStatisticValue = "-1";
        } else {
            this.invalidStatisticValue = "无效统计值";
        }
    }

    public GroupStatisticMethodEnum getStatisticMethod() {
        return statisticMethod;
    }

    public String getInvalidStatisticValue() {
        return invalidStatisticValue;
    }

    public boolean isSupportDatabaseStatistic() {
        return DB_STATISTIC_METHODS.contains(statisticMethod);
    }

    public boolean isDifferenceStatisticValue() {
        return GroupStatisticMethodEnum.TIME_RANGE_DAY.equals(statisticMethod)
                || GroupStatisticMethodEnum.TIME_RANGE_MONTH.equals(statisticMethod)
                || GroupStatisticMethodEnum.TIME_RANGE_YEAR.equals(statisticMethod);
    }

    public String getAsMinField() {
        return asMinField;
    }

    public String getAsMaxField() {
        return asMaxField;
    }

    protected <T> void withStatistic(TableGroupingFieldQuery query, QueryWrapper<T> queryWrapper) {
        switch (statisticMethod) {
            case MIN:
            case EARLIEST_TIME:
                queryWrapper.select("MIN(" + column + ") AS " + asField);
                query.withWhere(queryWrapper);
                break;
            case MAX:
            case LATEST_TIME:
                queryWrapper.select("MAX(" + column + ") AS " + asField);
                query.withWhere(queryWrapper);
                break;
            case TIME_RANGE_DAY:
            case TIME_RANGE_MONTH:
            case TIME_RANGE_YEAR:
                queryWrapper.select("MIN(" + column + ") AS " + asMinField, "MAX(" + column + ") AS " + asMaxField);
                query.withWhere(queryWrapper);
                break;
            case SUM:
                queryWrapper.select("SUM(" + column + ") AS " + asField);
                query.withWhere(queryWrapper);
                break;
            case AVERAGE:
                queryWrapper.select("SUM(" + column + ") / COUNT(1) AS " + asField);
                query.withWhere(queryWrapper);
                break;
        }
    }
}

package pro.shushi.pamirs.grouping.entity;

import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 表格分组统计字段查询
 *
 * @author Adamancy Zhang at 17:53 on 2025-11-18
 */
public class TableGroupingStatisticFieldQuery extends BasicTableGroupingFieldQuery {

    private final GroupStatisticMethodEnum statisticMethod;

    TableGroupingStatisticFieldQuery(String model, String field, GroupStatisticMethodEnum statisticMethod) {
        super(model, field);
        this.statisticMethod = statisticMethod;
    }

    GroupStatisticMethodEnum getStatisticMethod() {
        return statisticMethod;
    }

    <T> void withStatistic(QueryWrapper<T> queryWrapper) {
        switch (statisticMethod) {
            case COUNT:
                queryWrapper.select("COUNT(1) AS " + asField);
                break;
            case NULL:
                queryWrapper.select("COUNT(1) AS " + asField);
                if (isStringField()) {
                    queryWrapper.and(w -> w.isNull(column).or().eq(column, CharacterConstants.SEPARATOR_EMPTY));
                } else {
                    queryWrapper.isNull(column);
                }
                break;
            case NOT_NULL:
                queryWrapper.select("COUNT(1) AS " + asField);
                if (isStringField()) {
                    queryWrapper.isNotNull(column).ne(column, CharacterConstants.SEPARATOR_EMPTY);
                } else {
                    queryWrapper.isNull(column);
                }
                break;
            case EARLIEST_TIME:
            case MIN:
                queryWrapper.select("MIN(" + column + ") AS " + asField);
                break;
            case LATEST_TIME:
            case MAX:
                queryWrapper.select("MAX(" + column + ") AS " + asField);
                break;
//            case TIME_RANGE_DAY:
//            case TIME_RANGE_MONTH:
//            case TIME_RANGE_YEAR:
//                queryWrapper.select( "MIN(" + column + ") AS " + asField);
//                queryWrapper.select( "MAX(" + column + ") AS " + asField);
//                break;
            case SUM:
                queryWrapper.select("SUM(" + column + ") AS " + asField);
                break;
            case AVERAGE:
                queryWrapper.select("SUM(" + column + ") / COUNT(1) AS " + asField);
                break;
        }
    }
}

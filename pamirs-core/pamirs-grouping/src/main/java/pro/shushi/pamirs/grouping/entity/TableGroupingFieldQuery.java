package pro.shushi.pamirs.grouping.entity;

import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.model.GroupingField;
import pro.shushi.pamirs.grouping.model.GroupingStatisticField;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Collection;
import java.util.List;

/**
 * 表格分组字段查询
 *
 * @author Adamancy Zhang at 10:00 on 2025-11-13
 */
@Slf4j
public class TableGroupingFieldQuery extends BasicTableGroupingFieldQuery {

    private final SortDirectionEnum direction;

    private final String valueKey;

    private final TableGroupingStatisticFieldQuery statisticQuery;

    public TableGroupingFieldQuery(TableGroupingModel model, GroupingField field, boolean grouping) {
        this(model, field, grouping, null);
    }

    public TableGroupingFieldQuery(TableGroupingModel model, GroupingField field, boolean grouping, GroupingStatisticField statisticField) {
        this(model, field, grouping, false, statisticField);
    }

    public TableGroupingFieldQuery(TableGroupingModel model, GroupingField field, boolean grouping, boolean basic, GroupingStatisticField statisticField) {
        super(model, field.getField(), field.getValue(), grouping, basic);
        SortDirectionEnum direction = field.getDirection();
        if (direction == null) {
            direction = SortDirectionEnum.ASC;
        }
        this.direction = direction;
        this.valueKey = TableGroupingDataHelper.getGroupKeyByClientValue(this, this.value);
        if (statisticField == null || grouping) {
            this.statisticQuery = null;
        } else {
            this.statisticQuery = new TableGroupingStatisticFieldQuery(model, statisticField.getField(), statisticField.getStatisticMethod());
        }
    }

    public String getValueKey() {
        return valueKey;
    }

    public SortDirectionEnum getDirection() {
        return direction;
    }

    public TableGroupingStatisticFieldQuery getStatisticQuery() {
        return statisticQuery;
    }

    public <T> void withStatistic(QueryWrapper<T> queryWrapper) {
        statisticQuery.withStatistic(this, queryWrapper);
    }

    public <T> void withSelect(QueryWrapper<T> queryWrapper) {
        WrapperHelper.withSelect(queryWrapper, getColumnAsField());
    }

    public <T> void withGroupBy(QueryWrapper<T> queryWrapper) {
        if (isRelationOneField()) {
            List<String> relationColumns = getRelationColumns();
            for (String relationColumn : relationColumns) {
                queryWrapper.groupBy(relationColumn);
            }
        } else {
            queryWrapper.groupBy(column);
        }
    }

    public <T> void withOrderBy(QueryWrapper<T> queryWrapper) {
        if (isRelationOneField()) {
            List<String> relationColumns = getRelationColumns();
            for (String relationColumn : relationColumns) {
                withOrderBy(queryWrapper, relationColumn, direction);
            }
        } else {
            withOrderBy(queryWrapper, column, direction);
        }
    }

    public <T> void withOrderBy(QueryWrapper<T> queryWrapper, String column) {
        withOrderBy(queryWrapper, column, direction);
    }

    private <T> void withOrderBy(QueryWrapper<T> queryWrapper, String column, SortDirectionEnum direction) {
        switch (direction) {
            case ASC:
                queryWrapper.orderByAsc(column);
                break;
            case DESC:
                queryWrapper.orderByDesc(column);
                break;
            default:
                throw new UnsupportedOperationException("Invalid sort direction enumeration. direction: " + direction);
        }
    }

    /**
     * 追加单表 where 条件到指定 wrapper
     * <p>
     * 使用前需要使用 isSingleTableQuery 方法判断是否可用
     */
    public <T> void withWhere(QueryWrapper<T> queryWrapper) {
        if (value == null) {
            withNullWhere(queryWrapper);
            return;
        }
        if (isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (isEnumField() && isBitDataDictionary()) {
                long bitValue = 0L;
                for (Object item : coll) {
                    bitValue |= Long.parseLong(getDataDictionaryValue(String.valueOf(item)));
                }
                queryWrapper.eq(column, bitValue);
            }
        } else {
            if (isEnumField()) {
                queryWrapper.eq(column, getDataDictionaryValue(String.valueOf(value)));
            } else if (isRelationOneField()) {
                for (int i = 0; i < referenceFields.size(); i++) {
                    String referenceField = referenceFields.get(i);
                    String relationColumn = relationColumns.get(i);
                    Object referenceValue = FieldUtils.getFieldValue(value, referenceField);
                    if (referenceValue == null) {
                        queryWrapper.isNull(relationColumn);
                    } else {
                        queryWrapper.eq(relationColumn, referenceValue);
                    }
                }
            } else {
                queryWrapper.eq(column, value);
            }
        }
    }
}

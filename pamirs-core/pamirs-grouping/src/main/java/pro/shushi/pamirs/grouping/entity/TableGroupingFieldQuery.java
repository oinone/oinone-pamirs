package pro.shushi.pamirs.grouping.entity;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.grouping.model.GroupingField;
import pro.shushi.pamirs.grouping.model.GroupingStatisticField;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 表格分组字段查询
 *
 * @author Adamancy Zhang at 10:00 on 2025-11-13
 */
@Slf4j
public class TableGroupingFieldQuery extends BasicTableGroupingFieldQuery {

    private final TableGroupingFieldQuery parent;

    private final String model;

    private final String valueKey;

    private final SortDirectionEnum direction;

    private final TableGroupingStatisticFieldQuery statisticFieldQuery;

    public TableGroupingFieldQuery(String model, GroupingField field, TableGroupingFieldQuery parent) {
        this(model, field, null, parent);
    }

    public TableGroupingFieldQuery(String model, GroupingField field, GroupingStatisticField statisticField) {
        this(model, field, statisticField, null);
    }

    public TableGroupingFieldQuery(String model, GroupingField field, GroupingStatisticField statisticField, TableGroupingFieldQuery parent) {
        super(model, field.getField(), field.getValue());
        this.parent = parent;
        this.model = model;
        if (statisticField == null) {
            this.statisticFieldQuery = null;
        } else {
            this.statisticFieldQuery = new TableGroupingStatisticFieldQuery(model, statisticField.getField(), statisticField.getStatisticMethod());
        }
        SortDirectionEnum direction = field.getDirection();
        if (direction == null) {
            direction = SortDirectionEnum.ASC;
        }
        this.direction = direction;
        this.valueKey = TableGroupingDataHelper.getGroupKeyByClientValue(this, this.value);
    }

    public TableGroupingFieldQuery getParent() {
        return parent;
    }

    public String getModel() {
        return model;
    }

    public String getValueKey() {
        return valueKey;
    }

    public SortDirectionEnum getDirection() {
        return direction;
    }

    public String getStatisticField() {
        return statisticFieldQuery.getField();
    }

    public GroupStatisticMethodEnum getStatisticMethod() {
        return statisticFieldQuery.getStatisticMethod();
    }

    public <T> void withGroupBy(QueryWrapper<T> queryWrapper) {
        WrapperHelper.withSelect(queryWrapper, getColumnAsField());
        if (isRelationOneField()) {
            List<String> relationColumns = getRelationColumns();
            for (String relationColumn : relationColumns) {
                queryWrapper.groupBy(relationColumn);
                withOrderBy(queryWrapper, relationColumn, direction);
            }
        } else {
            queryWrapper.groupBy(column);
            withOrderBy(queryWrapper, column, direction);
        }
    }

    public <T> void withOrderBy(QueryWrapper<T> queryWrapper) {
        withOrderBy(queryWrapper, column, direction);
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
                throw new UnsupportedOperationException("Invalid sort direction enumeration.");
        }
    }

    /**
     * 追加单表 where 条件到指定 wrapper
     * <p>
     * 使用前需要使用 isSingleTableQuery 方法判断是否可用
     */
    public <T> void withWhere(QueryWrapper<T> queryWrapper) {
        if (value == null) {
            if (isStringField()) {
                queryWrapper.and(w -> w.isNull(column).or().eq(column, CharacterConstants.SEPARATOR_EMPTY));
            } else if (isRelationOneField()) {
                for (int i = 0; i < relationFields.size(); i++) {
                    String relationColumn = relationColumns.get(i);
                    queryWrapper.isNull(relationColumn);
                }
            } else {
                queryWrapper.isNull(column);
            }
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

    public <T> void withStatistic(QueryWrapper<T> queryWrapper) {
        statisticFieldQuery.withStatistic(queryWrapper);
    }
}

package pro.shushi.pamirs.grouping.statistic.defaults;

import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.entity.TableGroupingModel;
import pro.shushi.pamirs.grouping.model.GroupingField;
import pro.shushi.pamirs.grouping.statistic.AbstractStatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticApi;
import pro.shushi.pamirs.grouping.statistic.StatisticField;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * 统计唯一值数量
 *
 * @author Adamancy Zhang at 15:51 on 2025-11-20
 */
public class StatisticUnique<T> extends AbstractStatisticApi<T> implements StatisticApi<T> {

    private final TableGroupingFieldQuery query;

    private final Set<String> repeatSet = new HashSet<>();

    public StatisticUnique(StatisticField statisticField) {
        super(statisticField);
        // FIXME: zbh 20251120 此处统计不应该借用分组功能实现，目前未独立统计模块，暂时使用当前实现方案
        this.query = new TableGroupingFieldQuery(
                new TableGroupingModel(getModel()),
                new GroupingField().setField(getField()),
                true, true, null
        );
    }

    @Override
    protected void compute(T data, Object value) {
        String key = TableGroupingDataHelper.getGroupKeyByData(query, data);
        if (TableGroupingDataHelper.NULL_VALUE.equals(key)) {
            return;
        }
        repeatSet.add(key);
    }

    @Override
    public String getResult() {
        return String.valueOf(repeatSet.size());
    }

    public int getValue() {
        return repeatSet.size();
    }
}

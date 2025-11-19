package pro.shushi.pamirs.grouping.query.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingHelper;

import java.util.List;

/**
 * 万能的表格数据查询
 *
 * @author Adamancy Zhang at 12:16 on 2025-11-17
 */
@Order(999)
@Component
public class TableGroupingDataUniversalQuery<T> implements TableGroupingDataQueryApi<T> {

    @Override
    public boolean match(TableGroupingQueryContext<T> context) {
        return true;
    }

    @Override
    public List<T> queryGroupingDataByWrapper(TableGroupingQueryContext<T> context) {
        return TableGroupingHelper.fetchGroupingDataList(context.getQueryList(), context.generatorQueryWrapperWithOrderBy());
    }
}

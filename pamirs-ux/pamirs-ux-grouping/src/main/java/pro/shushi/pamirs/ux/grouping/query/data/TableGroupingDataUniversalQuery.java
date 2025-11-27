package pro.shushi.pamirs.ux.grouping.query.data;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.ux.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.ux.grouping.query.TableGroupingQueryContext;
import pro.shushi.pamirs.ux.grouping.utils.TableGroupingHelper;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
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
        List<TableGroupingFieldQuery> queryList = context.getQueryList();
        List<TableGroupingFieldQuery> memoryQueryList = new ArrayList<>();
        QueryWrapper<T> queryWrapper = context.generatorQueryWrapperWithOrderBy();
        for (TableGroupingFieldQuery query : queryList) {
            if (query.isSingleTableQuery()) {
                query.withWhere(queryWrapper);
            } else {
                memoryQueryList.add(query);
            }
        }
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (CollectionUtils.isEmpty(list) || memoryQueryList.isEmpty()) {
            return list;
        }
        for (TableGroupingFieldQuery memoryQuery : memoryQueryList) {
            if (memoryQuery.isSupportRelationQuery()) {
                list = Models.origin().listFieldQuery(list, memoryQuery.getField());
            }
        }
        return TableGroupingHelper.filter(list, memoryQueryList);
    }
}

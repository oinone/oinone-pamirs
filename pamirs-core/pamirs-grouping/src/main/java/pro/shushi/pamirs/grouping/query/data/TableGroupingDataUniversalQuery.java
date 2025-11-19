package pro.shushi.pamirs.grouping.query.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.query.grouping.TableGroupingQueryContext;
import pro.shushi.pamirs.grouping.utils.TableGroupingDataHelper;
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
            } else if (query.isRelationManyField()) {
                // FIXME: zbh 20251118 X2M 特殊处理逻辑
            } else {
                memoryQueryList.add(query);
            }
        }
        // FIXME: zbh 20251117 此处需使用 queryPage 查询数据
        List<T> list = Models.origin().queryListByWrapper(queryWrapper);
        if (memoryQueryList.isEmpty()) {
            return list;
        }
        return filter(context, memoryQueryList, list);
    }

    protected List<T> filter(TableGroupingQueryContext<T> context, List<TableGroupingFieldQuery> queryList, List<T> list) {
        for (TableGroupingFieldQuery query : queryList) {
            list = filter(query, list);
        }
        return list;
    }

    protected List<T> filter(TableGroupingFieldQuery query, List<T> list) {
        String valueKey = query.getValueKey();
        List<T> results = new ArrayList<>();
        for (T data : list) {
            if (valueKey.equals(TableGroupingDataHelper.getGroupKeyByData(query, data))) {
                results.add(data);
            }
        }
        return results;
    }
}

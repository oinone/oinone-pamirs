package pro.shushi.pamirs.grouping.service;

import pro.shushi.pamirs.grouping.model.TableGroupingResult;
import pro.shushi.pamirs.grouping.model.TableGroupingWrapper;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

import java.util.List;

/**
 * 分组服务
 *
 * @author Gesi at 17:10 on 2025/9/1
 */
public interface TableGroupingService {

    /**
     * 获取分组结构（当前页数据量低于某值时同时返回数据）
     */
    TableGroupingResult queryGroupingPage(Pagination<?> page, TableGroupingWrapper wrapper);

    /**
     * 根据请求分组路径获取分组数据
     */
    <T> List<T> queryGroupingDataByWrapper(TableGroupingWrapper wrapper);

    /**
     * 根据请求分组路径获取分组统计函数结果
     */
    String queryGroupingStatistic(TableGroupingWrapper wrapper);

}

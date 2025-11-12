package pro.shushi.pamirs.grouping.service;

import pro.shushi.pamirs.grouping.model.GroupResult;
import pro.shushi.pamirs.grouping.model.Grouping;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

/**
 * 分组服务
 *
 * @author Gesi at 17:10 on 2025/9/1
 */
public interface GroupingService {

    /**
     * 获取分组结构（当前页数据量低于某值时同时返回数据）
     */
    <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page);

    /**
     * 根据请求分组路径获取分组数据
     */
    <T> GroupResult<T> fetchGroupData(Grouping<T> group);

    /**
     * 根据请求分组路径获取分组统计函数结果
     */
    <T> GroupResult<T> fetchGroupStatistic(Grouping<T> group);

}

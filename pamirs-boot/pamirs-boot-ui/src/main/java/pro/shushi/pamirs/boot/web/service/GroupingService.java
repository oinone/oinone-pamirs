package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.tmodel.GroupResult;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;

/**
 * 分组服务
 *
 * @author Gesi at 17:10 on 2025/9/1
 */
public interface GroupingService {

    <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page);

    <T> GroupResult<T> fetchGroupData(Grouping<T> group);

    <T> GroupResult<T> fetchGroupStatistic(Grouping<T> group);

}

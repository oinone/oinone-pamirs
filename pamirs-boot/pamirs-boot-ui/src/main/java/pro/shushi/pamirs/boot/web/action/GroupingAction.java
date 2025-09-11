package pro.shushi.pamirs.boot.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupResult;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.service.GroupingService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 分组动作
 *
 * @author Gesi at 16:42 on 2025/9/1
 */
@Base
@Component
@Model.model(Grouping.MODEL_MODEL)
public class GroupingAction {

    @Autowired
    private GroupingService groupingService;

    @Function.Advanced(displayName = "分页查询分组信息", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public <T> GroupResult<T> fetchGroupPage(Grouping<T> group, Pagination<T> page) {
        return groupingService.fetchGroupPage(group, page);
    }

    @Function.Advanced(displayName = "查询分组数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public <T> GroupResult<T> fetchGroupData(Grouping<T> group) {
        return groupingService.fetchGroupData(group);
    }

}

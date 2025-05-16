package pro.shushi.pamirs.channel.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.EnhanceModel;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.API;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.REMOTE;

/**
 * EnhanceModelAction
 *
 * @author yakir on 2022/09/16 13:07.
 */
@Component
@Model.model(EnhanceModel.MODEL_MODEL)
public class EnhanceModelAction {

    @Function.Advanced(displayName = "根据条件分页查询记录列表和总数", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public <T> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
        return Fun.run(queryWrapper.getModel(), "search", page, queryWrapper);
    }
}

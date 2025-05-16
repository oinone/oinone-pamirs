package pro.shushi.pamirs.framework.orm;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 查询模型数据的默认过滤条件的Fun默认实现
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/3/26
 */
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultReadFiltersApi implements FunctionConstants {

    @Function.Advanced(displayName = "查询模型数据的默认过滤条件", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {LOCAL})
    @Function.fun(queryFilters)
    public String queryFilters() {
        return null;
    }


}

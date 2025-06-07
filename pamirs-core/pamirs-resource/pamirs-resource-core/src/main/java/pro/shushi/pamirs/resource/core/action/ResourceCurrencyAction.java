package pro.shushi.pamirs.resource.core.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;
import pro.shushi.pamirs.resource.api.model.ResourceCurrency;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * @author Adamancy Zhang on 2021-03-12 09:51
 */
@Component
@Model.model(ResourceCurrency.MODEL_MODEL)
public class ResourceCurrencyAction {

    @Action(displayName = "激活")
    @Action.Advanced(invisible = "context.activeRecord.active")
    @Function(openLevel = {LOCAL, REMOTE, API})
    public ResourceCurrency active(ResourceCurrency data) {
        data = fetchData(data);
        Boolean active = data.getActive();
        if (active == null || !active) {
            new ResourceCurrency().setActive(true).setId(data.getId()).updateById();
        }
        return data;
    }

    @Action(displayName = "取消激活")
    @Action.Advanced(invisible = "!context.activeRecord.active")
    @Function(openLevel = {LOCAL, REMOTE, API})
    public ResourceCurrency cancel(ResourceCurrency data) {
        data = fetchData(data);
        Boolean active = data.getActive();
        if (active == null || active) {
            new ResourceCurrency().setActive(false).setId(data.getId()).updateById();
        }
        return data;
    }

    private ResourceCurrency fetchData(ResourceCurrency data) {
        data = FetchUtil.fetchOne(data);
        if (data == null) {
            throw PamirsException.construct(ExpEnumerate.RESOURCE_CURRENCY_NOTEXIST).errThrow();
        }
        return data;
    }
}

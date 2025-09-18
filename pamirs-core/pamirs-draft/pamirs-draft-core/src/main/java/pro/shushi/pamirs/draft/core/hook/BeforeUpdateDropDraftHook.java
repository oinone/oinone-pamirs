package pro.shushi.pamirs.draft.core.hook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.util.Collection;

/**
 * 创建或删除前删除草稿hook
 *
 * @author Gesi at 11:13 on 2025/9/18
 */
@Component
public class BeforeUpdateDropDraftHook implements HookBefore {

    @Hook(fun = {
            FunctionConstants.create, FunctionConstants.update,
            FunctionConstants.createOne, FunctionConstants.createOrUpdate,
            FunctionConstants.createOrUpdateWithResult,
            FunctionConstants.updateByPk, FunctionConstants.updateByEntity
    })
    @Override
    public Object run(Function function, Object... args) {
        if (args.length == 0) {
            return args;
        }
        Object data = args[0];
        if (data != null && !(data instanceof Collection)) {
            String model = Models.api().getDataModel(data);
            if (StringUtils.isNotBlank(model)) {
                Fun.run(model, "deleteDraft", data);
            }
        }
        return args;
    }
}

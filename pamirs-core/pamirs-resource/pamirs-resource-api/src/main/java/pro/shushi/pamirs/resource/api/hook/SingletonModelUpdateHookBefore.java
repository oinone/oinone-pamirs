package pro.shushi.pamirs.resource.api.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.resource.api.model.SingletonModel;

@Base
@Component
public class SingletonModelUpdateHookBefore implements HookBefore {

    @Override
    @Hook(priority = Integer.MAX_VALUE, displayName = "处理SingletonModel的修改")
    public Object run(Function function, Object... args) {
        if (!FunctionConstants.updateByPk.equals(function.getFun())) {
            return args;
        }
        if (args == null) {
            return args;
        }
        if (!(((Object[]) args)[0] instanceof SingletonModel)) {
            return args;
        }
        ((SingletonModel) ((Object[]) args)[0]).cleanCache();
//        ((SingletonModel)((Object[])args)[0]).singletonModel();
        return args;
    }
}

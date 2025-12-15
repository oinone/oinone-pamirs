package pro.shushi.pamirs.ux.draft.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.ux.draft.session.DraftSession;
import pro.shushi.pamirs.ux.draft.session.DraftSessionContext;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 删除草稿后置处理
 *
 * @author Adamancy Zhang at 14:40 on 2025-10-20
 */
@Base
@Component
public class DeleteDraftHookAfter implements HookAfter {

    @Hook(functionTypes = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE, FunctionTypeEnum.DELETE}, priority = Integer.MAX_VALUE)
    @Override
    public Object run(Function function, Object ret) {
        DraftSessionContext context = DraftSession.get();
        if (context == null) {
            return ret;
        }
        Function deleteFunction = context.getDelettFunction();
        if (deleteFunction != null) {
            Fun.run(deleteFunction, context.getDraft().getCode());
        }
        return ret;
    }
}

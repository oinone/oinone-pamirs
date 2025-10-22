package pro.shushi.pamirs.draft.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.draft.session.DraftSession;
import pro.shushi.pamirs.draft.session.DraftSessionContext;
import pro.shushi.pamirs.draft.spi.DraftStrategyApi;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * 删除草稿参数预处理
 *
 * @author Adamancy Zhang at 14:40 on 2025-10-20
 */
@Base
@Component
public class DeleteDraftHookBefore implements HookBefore {

    @Hook(functionTypes = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE, FunctionTypeEnum.DELETE}, priority = Integer.MAX_VALUE)
    @Override
    public Object run(Function function, Object... args) {
        if (DraftSession.get() != null) {
            return null;
        }
        DraftSessionContext context = DraftStrategyApi.HOLDER.get().loadSession(function, args);
        if (context != null) {
            DraftSession.set(context);
        }
        return null;
    }
}

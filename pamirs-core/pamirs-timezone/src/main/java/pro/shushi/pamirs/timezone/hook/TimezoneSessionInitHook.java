package pro.shushi.pamirs.timezone.hook;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.timezone.session.TimezoneSession;

/**
 * 时区上下文初始化
 *
 * @author Adamancy Zhang at 16:03 on 2021-09-03
 */
@Base
@Order
@Component
public class TimezoneSessionInitHook implements HookBefore {

    @Hook(priority = 35)
    @Override
    public Object run(Function function, Object... args) {
        TimezoneSession.initTimezone();
        return null;
    }
}

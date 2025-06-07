package pro.shushi.pamirs.timezone.hook;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.timezone.TimezoneConvertHelper;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.timezone.session.TimezoneSession;

import java.util.TimeZone;

/**
 * 时区转换
 *
 * @author Adamancy Zhang at 10:20 on 2021-09-03
 */
@Base
@Order
@Component
public class TimezoneHookAfter implements HookAfter {

    @Hook(priority = 999)
    @Override
    public Object run(Function function, Object ret) {
        TimeZone timezone = TimezoneSession.getTimezone();
        if (timezone == null) {
            return null;
        }
        if (ret == null) {
            return null;
        }
        if (ret instanceof Object[]) {
            ret = ((Object[]) ret)[0];
        }
        Object argument;
        if (ret instanceof Pagination) {
            argument = ((Pagination<?>) ret).getContent();
        } else {
            argument = ret;
        }
        if (argument == null) {
            return null;
        }
        TimezoneConvertHelper.convert(argument, timezone);
        return null;
    }
}

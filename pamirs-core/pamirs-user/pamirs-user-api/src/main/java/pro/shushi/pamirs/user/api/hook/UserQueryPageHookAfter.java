package pro.shushi.pamirs.user.api.hook;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.user.api.behavior.impl.UserNameBehavior;

import java.util.List;

@Base
@Component
public class UserQueryPageHookAfter implements HookAfter {

    @Override
    @Hook(priority = Integer.MAX_VALUE, displayName = "处理用户名")
    public Object run(Function function, Object ret) {
        if (!FunctionConstants.queryPage.equals(function.getFun())) {
            return ret;
        }
        if (ret == null) {
            return null;
        }
        Object data = null;
        if (ret instanceof Object[]) {
            Object[] rets = (Object[]) ret;
            if (rets.length == 1) {
                data = rets[0];
            }
        } else {
            data = ret;
        }
        if (data == null) {
            return ret;
        }
        if (!(data instanceof Pagination)) {
            return ret;
        }
        Pagination<?> pagination = (Pagination<?>) data;
        List<?> content = pagination.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return ret;
        }
        Object first = content.get(0);
        if (UserNameBehavior.isNeedSet(first, function.getNamespace())) {
            UserNameBehavior.set(content);
        }
        return ret;
    }
}

package pro.shushi.pamirs.auth.api.runtime.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.executor.FieldPermissionExecutor;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 字段权限Hook
 *
 * @author Adamancy Zhang at 16:24 on 2024-01-06
 */
@Base
@Slf4j
@Component
public class FieldPermissionHook implements HookAfter {

    @Hook
    @Override
    public Object run(Function function, Object ret) {
        return FieldPermissionExecutor.run(function, ret);
    }
}

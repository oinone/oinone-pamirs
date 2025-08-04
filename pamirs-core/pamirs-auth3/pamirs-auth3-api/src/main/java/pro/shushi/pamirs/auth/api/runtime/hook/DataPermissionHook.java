package pro.shushi.pamirs.auth.api.runtime.hook;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.executor.DataPermissionExecutor;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import jakarta.annotation.Resource;

/**
 * 数据权限Hook
 *
 * @author Adamancy Zhang at 16:21 on 2024-01-06
 */
@Base
@Slf4j
@Component
public class DataPermissionHook implements HookBefore {

    @Resource
    private MetaCacheManager metaCacheManager;

    @Hook(priority = 20)
    @Override
    public Object run(Function function, Object... args) {
        DataPermissionExecutor.prepareArguments(function, args);
        return function;
    }
}

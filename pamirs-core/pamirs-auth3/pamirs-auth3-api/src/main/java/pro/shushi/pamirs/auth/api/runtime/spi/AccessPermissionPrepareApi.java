package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 权限缓存预处理API
 *
 * @author Adamancy Zhang at 12:06 on 2024-01-31
 */
public interface AccessPermissionPrepareApi {

    void prepareAccessPermission(Function function, Object... args);
}

package pro.shushi.pamirs.meta.api.core.auth;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 权限接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface AuthApi extends CommonApi {

    Result<Void> canAccessModule(String moduleName);

}

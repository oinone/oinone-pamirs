package pro.shushi.pamirs.auth.api.behavior;

import pro.shushi.pamirs.auth.api.enumeration.AuthorizationSourceEnum;

/**
 * 权限数据来源
 *
 * @author Adamancy Zhang at 14:21 on 2024-01-04
 */
public interface AuthAuthorizationSource {

    /**
     * 获取权限数据来源
     *
     * @return 数据来源
     */
    AuthorizationSourceEnum getSource();
}

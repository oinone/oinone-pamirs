package pro.shushi.pamirs.auth.api.spi;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.auth.api.model.AuthUserRoleRel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 角色管理扩展API
 *
 * @author Adamancy Zhang at 13:45 on 2024-05-23
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthRoleManagerExtendApi {

    default void delete(List<AuthRole> roles, List<AuthUserRoleRel> userRoleRelList) {
    }

    default void active(AuthRole role, List<AuthUserRoleRel> userRoleRelList) {
    }

    default void disable(AuthRole role, List<AuthUserRoleRel> userRoleRelList) {
    }
}

package pro.shushi.pamirs.auth.api.extend.authorization;

import pro.shushi.pamirs.auth.api.pmodel.AuthFieldAuthorization;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Set;

/**
 * 字段权限授权扩展API
 *
 * @author Adamancy Zhang at 16:01 on 2024-09-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthFieldAuthorizationExtendApi extends AuthAuthorizationSceneApi {

    void updates(Set<Long> roleIds, List<AuthFieldAuthorization> permissions);

}

package pro.shushi.pamirs.auth.api.service.manager;

import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.List;

/**
 * 权限路径映射管理
 *
 * @author Adamancy Zhang at 16:27 on 2024-03-25
 */
@Fun(AuthPathMappingManager.FUN_NAMESPACE)
public interface AuthPathMappingManager {

    String FUN_NAMESPACE = "auth.AuthPathMappingManager";

    @Function
    List<AuthPathMapping> collectionPathMappings(List<AuthPathMapping> pathMappings);

}

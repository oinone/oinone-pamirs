package pro.shushi.pamirs.framework.common.api;

import java.util.Set;

/**
 * PlatformJarVersionCheckerApi
 *
 * @author yakir on 2024/07/24 10:25.
 */
public interface PlatformJarVersionCheckerApi {

    void init(Boolean goBack);

    void jarVersion(Set<Class<?>> moduleClazzSet);

    void compare();

    void store();

}

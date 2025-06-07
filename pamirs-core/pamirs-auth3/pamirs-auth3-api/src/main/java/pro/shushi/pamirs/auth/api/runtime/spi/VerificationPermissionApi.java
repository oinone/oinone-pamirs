package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 验证权限API
 *
 * @author Adamancy Zhang at 10:44 on 2024-01-29
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface VerificationPermissionApi {

    Boolean isAccessModule(Set<String> accessModules, String module);

    Boolean isAccessHomepage(Set<String> accessHomepages, String module);

    Boolean isAccessMenu(Set<String> accessMenus, String module, String name);

    Boolean isAccessFunction(String namespace, String fun);

    Boolean isAccessAction(Set<String> accessActions, String model, String name);

    Boolean isAccessAction(Set<String> accessActions, String actionPath);

    Boolean isManagementModule(Set<String> managementModules, String module);

    Boolean isManagementHomepage(Set<String> managementHomepageModules, String module);

    Boolean isManagementMenu(Set<String> managementMenus, String module, String name);

    Boolean isManagementAction(Set<String> accessActions, String model, String name);

    Boolean isManagementAction(Set<String> accessActions, String path);
}

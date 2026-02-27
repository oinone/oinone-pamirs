package pro.shushi.pamirs.auth.api.runtime.spi.defaults;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.spi.VerificationPermissionApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 默认验证权限实现
 *
 * @author Adamancy Zhang at 20:51 on 2026-02-27
 */
@Order
@Component
@SPI.Service
public class DefaultVerificationPermissionApi implements VerificationPermissionApi {

    @Override
    public Boolean isAccessModule(Set<String> accessModules, String module) {
        return true;
    }

    @Override
    public Boolean isAccessHomepage(Set<String> accessHomepages, String module) {
        return true;
    }

    @Override
    public Boolean isAccessMenu(Set<String> accessMenus, String module, String name) {
        return true;
    }

    @Override
    public Boolean isAccessFunction(String namespace, String fun) {
        return true;
    }

    @Override
    public Boolean isAccessAction(Set<String> accessActions, String model, String name) {
        return true;
    }

    @Override
    public Boolean isAccessAction(Set<String> accessActions, String actionPath) {
        return true;
    }

    @Override
    public Boolean isManagementModule(Set<String> managementModules, String module) {
        return true;
    }

    @Override
    public Boolean isManagementHomepage(Set<String> managementHomepageModules, String module) {
        return true;
    }

    @Override
    public Boolean isManagementMenu(Set<String> managementMenus, String module, String name) {
        return true;
    }

    @Override
    public Boolean isManagementAction(Set<String> accessActions, String model, String name) {
        return true;
    }

    @Override
    public Boolean isManagementAction(Set<String> accessActions, String path) {
        return true;
    }
}

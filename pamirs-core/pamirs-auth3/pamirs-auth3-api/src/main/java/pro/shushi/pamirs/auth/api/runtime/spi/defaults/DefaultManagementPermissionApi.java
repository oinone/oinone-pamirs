package pro.shushi.pamirs.auth.api.runtime.spi.defaults;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.spi.ManagementPermissionApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Set;

/**
 * 默认管理权限实现
 *
 * @author Adamancy Zhang at 12:37 on 2026-02-26
 */
@Order
@Component
@SPI.Service
public class DefaultManagementPermissionApi implements ManagementPermissionApi {

    @Override
    public AuthResult<Set<String>> fetchManagementModules() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchManagementHomepages() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchManagementMenus(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchManagementMenus(Set<String> modules) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchManagementActions(String model) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isManagementModule(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isManagementHomepage(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isManagementMenu(String module, String name) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isManagementAction(String model, String name) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isManagementAction(String path) {
        return AuthResult.success();
    }
}

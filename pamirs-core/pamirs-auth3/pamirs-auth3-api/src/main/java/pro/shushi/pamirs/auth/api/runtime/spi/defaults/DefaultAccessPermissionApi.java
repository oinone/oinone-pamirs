package pro.shushi.pamirs.auth.api.runtime.spi.defaults;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.configure.AuthConfiguration;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.runtime.spi.AccessPermissionApi;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 默认访问权限验证实现
 *
 * @author Adamancy Zhang at 22:33 on 2026-02-25
 */
@Order
@Component
@SPI.Service
public class DefaultAccessPermissionApi implements AccessPermissionApi {

    @Autowired
    private AuthConfiguration authConfiguration;

    @Override
    public AuthResult<Set<String>> fetchAccessModules() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchAccessHomepages() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchAccessMenus(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Map<String, Set<String>>> fetchAccessMenus(Set<String> modules) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions() {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Set<String>> fetchAccessActions(String model) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isAccessModule(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isAccessHomepage(String module) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isAccessMenu(String module, String name) {
        return AuthResult.success();
    }

    @Override
    public Boolean isFilterFunction(String namespace, String fun) {
        if (AuthHelper.isFunctionInWhite(namespace, fun)) {
            return Boolean.TRUE;
        }
        return Optional.ofNullable(authConfiguration.getFunFilter())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.stream().anyMatch(vv -> vv.getNamespace().equals(namespace) && vv.getFun().equals(fun)))
                .orElse(Boolean.FALSE);
    }

    @Override
    public Boolean isFilterFunctionOnlyLogin(String namespace, String fun) {
        if (AuthHelper.isFunctionInWhiteOnlyLogin(namespace, fun)) {
            return Boolean.TRUE;
        }
        return Optional.ofNullable(authConfiguration.getFunFilterOnlyLogin())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.stream().anyMatch(vv -> vv.getNamespace().equals(namespace) && vv.getFun().equals(fun)))
                .orElse(Boolean.FALSE);
    }

    @Override
    public AuthResult<Boolean> isAccessFunction(String namespace, String fun) {
        if (isFilterFunction(namespace, fun)) {
            return AuthResult.success(Boolean.TRUE);
        }
        AuthVerificationHelper.checkLogin();
        if (isFilterFunctionOnlyLogin(namespace, fun)) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isAccessAction(String model, String name) {
        if (isFilterFunction(model, name)) {
            return AuthResult.success(Boolean.TRUE);
        }
        AuthVerificationHelper.checkLogin();
        if (isFilterFunctionOnlyLogin(model, name)) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthResult.success();
    }

    @Override
    public AuthResult<Boolean> isAccessAction(String path) {
        return AuthResult.success();
    }
}

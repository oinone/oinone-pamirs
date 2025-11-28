package pro.shushi.pamirs.auth.api.spi.service;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Set;

/**
 * 默认权限实现
 *
 * @author Adamancy Zhang at 12:06 on 2024-01-11
 */
@Slf4j
@Order(55)
@SPI.Service
public class DefaultAuthApi implements AuthApi {

    private static AuthAccessService INSTANCE;

    private AuthAccessService getAuthAccessService() {
        AuthAccessService authAccessService = DefaultAuthApi.INSTANCE;
        if (authAccessService == null) {
            synchronized (DefaultAuthApi.class) {
                authAccessService = DefaultAuthApi.INSTANCE;
                if (authAccessService == null) {
                    authAccessService = BeanDefinitionUtils.getBeansOfTypeByOrdered(AuthAccessService.class).get(0);
                    DefaultAuthApi.INSTANCE = authAccessService;
                }
            }
        }
        return authAccessService;
    }

    @Override
    public Result<Void> canAccessModule(String module) {
        return getAuthAccessService().canAccessModule(module);
    }

    @Override
    public Result<Void> canAccessHomepage(String module) {
        return getAuthAccessService().canAccessHomepage(module);
    }

    @Override
    public Result<Void> canAccessMenu(String module, String name) {
        return getAuthAccessService().canAccessMenu(module, name);
    }

    @Override
    public Result<Void> canAccessAction(String model, String name) {
        return getAuthAccessService().canAccessAction(model, name);
    }

    @Override
    public Result<Void> canAccessAction(String path) {
        return getAuthAccessService().canAccessAction(path);
    }

    @Override
    public Result<Void> canAccessFunction(String namespace, String fun) {
        return getAuthAccessService().canAccessFunction(namespace, fun);
    }

    @Override
    public Result<String> canReadableData(String model) {
        return getAuthAccessService().canReadableData(model);
    }

    @Override
    public Result<String> canWritableData(String model) {
        return getAuthAccessService().canWritableData(model);
    }

    @Override
    public Result<String> canDeletableData(String model) {
        return getAuthAccessService().canDeletableData(model);
    }

    @Override
    public Result<Set<String>> canReadableFields(String model) {
        return getAuthAccessService().canReadableFields(model);
    }

    @Override
    public Result<Set<String>> canWritableFields(String model) {
        return getAuthAccessService().canWritableFields(model);
    }

    @Override
    public String getDataFilter(String namespace, String fun) {
        return getAuthAccessService().getDataFilter(namespace, fun);
    }

    @Override
    public Result<Set<String>> canAccessModules() {
        return getAuthAccessService().canAccessModules();
    }

    @Override
    public Result<Set<String>> canAccessHomepages() {
        return getAuthAccessService().canAccessHomepages();
    }

    @Override
    public Result<Set<String>> canAccessMenus(String module) {
        return getAuthAccessService().canAccessMenus(module);
    }

    @Override
    public Result<Set<String>> canAccessActions() {
        return getAuthAccessService().canAccessActions();
    }

    @Override
    public Result<Set<String>> canAccessActions(String model) {
        return getAuthAccessService().canAccessActions(model);
    }
}

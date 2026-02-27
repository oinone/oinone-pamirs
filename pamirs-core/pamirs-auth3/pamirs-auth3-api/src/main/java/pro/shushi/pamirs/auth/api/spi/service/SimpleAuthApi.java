package pro.shushi.pamirs.auth.api.spi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.auth.api.configure.AuthConfiguration;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.boot.open.auth.DefaultAuthApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Optional;

/**
 * 简单权限校验
 *
 * @author Adamancy Zhang at 19:15 on 2026-02-25
 */
@Slf4j
@Order(100)
@SPI.Service
public class SimpleAuthApi extends DefaultAuthApi implements AuthApi {

    private static AuthConfiguration INSTANCE;

    private AuthConfiguration getAuthConfiguration() {
        AuthConfiguration authConfiguration = SimpleAuthApi.INSTANCE;
        if (authConfiguration == null) {
            synchronized (DefaultAuthApi.class) {
                authConfiguration = SimpleAuthApi.INSTANCE;
                if (authConfiguration == null) {
                    authConfiguration = BeanDefinitionUtils.getBean(AuthConfiguration.class);
                    SimpleAuthApi.INSTANCE = authConfiguration;
                }
            }
        }
        return authConfiguration;
    }

    @Override
    public Result<Void> canAccessFunction(String namespace, String fun) {
        if (isFilterFunction(namespace, fun)) {
            return new Result<>();
        }
        AuthVerificationHelper.checkLogin();
        return new Result<>();
    }

    private Boolean isFilterFunction(String namespace, String fun) {
        if (AuthHelper.isFunctionInWhite(namespace, fun)) {
            return Boolean.TRUE;
        }
        return Optional.ofNullable(getAuthConfiguration().getFunFilter())
                .filter(CollectionUtils::isNotEmpty)
                .map(v -> v.stream().anyMatch(vv -> vv.getNamespace().equals(namespace) && vv.getFun().equals(fun)))
                .orElse(Boolean.FALSE);
    }
}

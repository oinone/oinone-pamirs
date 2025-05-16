package pro.shushi.pamirs.framework.faas.spi.api.remote.utils;

import pro.shushi.pamirs.framework.faas.configure.PamirsFrameworkRemoteRegistryConfiguration;
import pro.shushi.pamirs.framework.faas.spi.api.remote.RemoteRegistryStrategyApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * dubbo的泛化调用的路由规则
 *
 * @author cpc on 2024-03-26
 * @version 1.0.0
 */
public class RegistryUtils {

    private static PamirsFrameworkRemoteRegistryConfiguration registryConfiguration;

    private static PamirsFrameworkRemoteRegistryConfiguration getRegistryConfiguration() {
        if (registryConfiguration != null) {
            return registryConfiguration;
        }
        registryConfiguration = BeanDefinitionUtils.getBean(PamirsFrameworkRemoteRegistryConfiguration.class);
        return registryConfiguration;
    }

    public static String getRegistryInterface(Function function) {
        String scope = getRegistryConfiguration().getServiceScope();
        RemoteRegistryStrategyApi remoteRegistryStrategyApi = Spider.getExtension(RemoteRegistryStrategyApi.class, scope);
        return remoteRegistryStrategyApi.getRegistryInterface(function);
    }

    public static String getFunctionNamespace(String method) {
        String scope = getRegistryConfiguration().getServiceScope();
        RemoteRegistryStrategyApi remoteRegistryStrategyApi = Spider.getExtension(RemoteRegistryStrategyApi.class, scope);
        return remoteRegistryStrategyApi.getFunctionNamespace(method);
    }

    public static String getFunctionFun(String method) {
        String scope = getRegistryConfiguration().getServiceScope();
        RemoteRegistryStrategyApi remoteRegistryStrategyApi = Spider.getExtension(RemoteRegistryStrategyApi.class, scope);
        return remoteRegistryStrategyApi.getFunctionFun(method);
    }

    public static String getGenericServiceMethodName(Function function) {
        String scope = getRegistryConfiguration().getServiceScope();
        RemoteRegistryStrategyApi remoteRegistryStrategyApi = Spider.getExtension(RemoteRegistryStrategyApi.class, scope);
        return remoteRegistryStrategyApi.getGenericServiceMethodName(function);
    }

}

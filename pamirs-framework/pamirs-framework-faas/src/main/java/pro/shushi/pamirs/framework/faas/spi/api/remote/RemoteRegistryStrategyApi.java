package pro.shushi.pamirs.framework.faas.spi.api.remote;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Arrays;
import java.util.List;


/**
 * 注册策略API
 *
 * @author cpc on 2024-03-26
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RemoteRegistryStrategyApi {

    String SCOPE_MODULE = "module";
    String SCOPE_NAMESPACE = "namespace";

    String DUBBO_SPLIT = ".oio.";

    List<String> scopes = Arrays.asList(SCOPE_MODULE, SCOPE_NAMESPACE);

    String getRegistryInterface(Function function);

    default String getFunctionNamespace(String method) {
        return method.split(DUBBO_SPLIT)[0];
    }

    default String getFunctionFun(String method) {
        return method.split(DUBBO_SPLIT)[1];
    }

    default String getGenericServiceMethodName(Function function) {
        return function.getNamespace() + DUBBO_SPLIT + function.getFun();
    }

}

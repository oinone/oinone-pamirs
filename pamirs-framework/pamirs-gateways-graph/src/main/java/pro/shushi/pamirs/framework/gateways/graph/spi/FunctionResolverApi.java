package pro.shushi.pamirs.framework.gateways.graph.spi;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * Function解析器API
 * 用于在GraphQL Schema构建时解析Function，支持虚拟动作等扩展
 *
 * @author yeshenyue on 2025/12/16 19:20.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface FunctionResolverApi {

    HoldKeeper<FunctionResolverApi> holder = new HoldKeeper<>();

    Function resolveFunction(String funNamespace, String funName);

    static FunctionResolverApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(FunctionResolverApi.class));
    }
}
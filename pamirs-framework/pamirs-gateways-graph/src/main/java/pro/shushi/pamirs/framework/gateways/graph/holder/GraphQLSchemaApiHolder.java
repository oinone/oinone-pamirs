package pro.shushi.pamirs.framework.gateways.graph.holder;

import pro.shushi.pamirs.framework.gateways.graph.spi.GraphQLSchemaApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * GraphQL协议构建API持有者
 *
 * @author Adamancy Zhang at 20:01 on 2024-10-24
 */
public class GraphQLSchemaApiHolder {

    private static final HoldKeeper<GraphQLSchemaApi> holder = new HoldKeeper<>();

    public static GraphQLSchemaApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(GraphQLSchemaApi.class));
    }
}

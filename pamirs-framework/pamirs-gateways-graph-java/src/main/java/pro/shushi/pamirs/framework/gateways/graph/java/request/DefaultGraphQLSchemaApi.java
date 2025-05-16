package pro.shushi.pamirs.framework.gateways.graph.java.request;

import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.configuration.PamirsFrameworkGatewayConfiguration;
import pro.shushi.pamirs.framework.gateways.graph.java.build.GraphQLBuilder;
import pro.shushi.pamirs.framework.gateways.graph.spi.GraphQLSchemaApi;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Collections;

/**
 * GraphQL协议构建API默认实现
 * <p>
 * 2021/3/26 7:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultGraphQLSchemaApi implements GraphQLSchemaApi {

    @Autowired
    private PamirsFrameworkGatewayConfiguration pamirsFrameworkGatewayConfiguration;

    @Override
    public GraphQLSchema build(PamirsRequestParam param) {
        GraphQLSchema schema = DefaultRequestExecutor.getSchema();
        if (schema == null) {
            // FIXME: zbh 20250221 使用持久化缓存
            String module = PamirsSession.getServApp();
            schema = GraphQLBuilder.buildGraphQLSchema(Collections.singleton(module), pamirsFrameworkGatewayConfiguration.isShowDoc());
        }
        return schema;
    }

    @Override
    public Boolean isDynamicBuild() {
        return Boolean.FALSE;
    }

}

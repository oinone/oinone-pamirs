package pro.shushi.pamirs.framework.gateways.graph.spi;

import graphql.schema.GraphQLSchema;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * GraphQL协议构建API
 * <p>
 * 2022/1/13 10:43 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface GraphQLSchemaApi {

    GraphQLSchema build(PamirsRequestParam param);

    Boolean isDynamicBuild();

}

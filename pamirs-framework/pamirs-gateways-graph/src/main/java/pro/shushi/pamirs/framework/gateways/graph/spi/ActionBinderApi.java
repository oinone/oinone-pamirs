package pro.shushi.pamirs.framework.gateways.graph.spi;

import graphql.schema.DataFetchingEnvironment;
import org.dataloader.BatchLoaderEnvironment;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 控制器请求执行api
 * <p>
 * 2021/3/29 3:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ActionBinderApi {

    Object action(ModelConfig modelConfig, Function function, boolean isQuery, DataFetchingEnvironment env);

    Object relationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment env);

    Object batchRelationQuery(ModelFieldConfig modelFieldConfig, DataFetchingEnvironment env);

    List<Object> relationQuery(List<String> keys, BatchLoaderEnvironment env);

}

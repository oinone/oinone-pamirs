package pro.shushi.pamirs.meta.api.core.orm.systems.orm;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.orm.OrmApi;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.common.spi.SPI;

import static pro.shushi.pamirs.meta.common.constants.NamespaceConstants.spiDefault;

/**
 * Orm api默认实现
 * <p>
 * 2020/7/3 11:10 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service(spiDefault)
public class DefaultOrmApi implements OrmApi {

    @Override
    public <T> Object modeling(String model, T obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::modeling,
                OrmProcessorHolder.modelingProcessor(),
                OrmProcessorHolder.modelingProcessor(),
                (context, fieldConfig, dMap) -> OrmProcessorHolder.modelingProcessor().run(this::modeling, fieldConfig, dMap));
    }

    @Override
    public <T> T mapping(String model, Object obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::mapping,
                OrmProcessorHolder.mappingProcessor(),
                OrmProcessorHolder.mappingProcessor(),
                (context, fieldConfig, dMap) -> OrmProcessorHolder.mappingProcessor().run(this::mapping, fieldConfig, dMap));
    }

    @Override
    public <T> T objecting(String model, Object map) {
        return DataComputeTemplate.getInstance().compute(model, map,
                this::objecting,
                OrmProcessorHolder.objectingProcessor(),
                OrmProcessorHolder.objectingProcessor(),
                (context, fieldConfig, dMap) -> OrmProcessorHolder.objectingProcessor().run(this::objecting, fieldConfig, dMap));
    }

}

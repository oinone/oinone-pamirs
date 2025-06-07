package pro.shushi.pamirs.framework.orm.api;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.OrmApi;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 不处理子层ORM转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Order(99)
@SPI.Service(NamespaceConstants.spiMono)
@Component
public class MonoOrmApi implements OrmApi {

    @Override
    public <T> T modeling(String model, T obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::modeling,
                (oModel, oObj) -> RecursionOrmApi.getOrmModelingProcessor().before(oModel, oObj),
                (oModel, oObj) -> RecursionOrmApi.getOrmModelingProcessor().after(oModel, oObj),
                (context, fieldConfig, dMap) -> {
                });
    }

    @Override
    public <T> T mapping(String model, Object obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::mapping,
                (oModel, oObj) -> RecursionOrmApi.getOrmMappingProcessor().before(oModel, oObj),
                (oModel, oObj) -> RecursionOrmApi.getOrmMappingProcessor().after(oModel, oObj),
                (context, fieldConfig, dMap) -> {
                });
    }

    @Override
    public <T> T objecting(String model, Object map) {
        return DataComputeTemplate.getInstance().compute(model, map,
                this::objecting,
                (oModel, oObj) -> RecursionOrmApi.getOrmObjectingProcessor().before(oModel, oObj),
                (oModel, oObj) -> RecursionOrmApi.getOrmObjectingProcessor().after(oModel, oObj),
                (context, fieldConfig, dMap) -> {
                });
    }

}

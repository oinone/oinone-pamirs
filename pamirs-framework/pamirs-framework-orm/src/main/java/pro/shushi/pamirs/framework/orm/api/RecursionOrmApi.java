package pro.shushi.pamirs.framework.orm.api;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
import pro.shushi.pamirs.framework.orm.processor.recursion.OrmMappingRecursionProcessor;
import pro.shushi.pamirs.framework.orm.processor.recursion.OrmModelingRecursionProcessor;
import pro.shushi.pamirs.framework.orm.processor.recursion.OrmObjectingRecursionProcessor;
import pro.shushi.pamirs.meta.api.core.orm.OrmApi;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 默认ORM转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Order(99)
@SPI.Service
public class RecursionOrmApi implements OrmApi {

    private static final OrmModelingProcessor ormModelingRecursionProcessor = new OrmModelingRecursionProcessor();

    private static final OrmMappingProcessor ormMappingRecursionProcessor = new OrmMappingRecursionProcessor();

    private static final OrmObjectingProcessor ormObjectingRecursionProcessor = new OrmObjectingRecursionProcessor();

    @Override
    public <T> T modeling(String model, T obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::modeling,
                ormModelingRecursionProcessor,
                ormModelingRecursionProcessor,
                (context, fieldConfig, dMap) -> ormModelingRecursionProcessor.run(this::modeling, fieldConfig, dMap));
    }

    @Override
    public <T> T mapping(String model, Object obj) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                this::mapping,
                ormMappingRecursionProcessor,
                ormMappingRecursionProcessor,
                (context, fieldConfig, dMap) -> ormMappingRecursionProcessor.run(this::mapping, fieldConfig, dMap));
    }

    @Override
    public <T> T objecting(String model, Object map) {
        return DataComputeTemplate.getInstance().compute(model, map,
                this::objecting,
                ormObjectingRecursionProcessor,
                ormObjectingRecursionProcessor,
                (context, fieldConfig, dMap) -> ormObjectingRecursionProcessor.run(this::objecting, fieldConfig, dMap));
    }

    public static OrmModelingProcessor getOrmModelingProcessor() {
        return ormModelingRecursionProcessor;
    }

    public static OrmMappingProcessor getOrmMappingProcessor() {
        return ormMappingRecursionProcessor;
    }

    public static OrmObjectingProcessor getOrmObjectingProcessor() {
        return ormObjectingRecursionProcessor;
    }

}

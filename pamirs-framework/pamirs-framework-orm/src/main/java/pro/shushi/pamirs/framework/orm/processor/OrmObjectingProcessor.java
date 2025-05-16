package pro.shushi.pamirs.framework.orm.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.systems.orm.OrmProcessorHolder;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldRecursionComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelBeforeComputeApi;
import pro.shushi.pamirs.meta.api.core.orm.template.function.PersistenceModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * ORM对象化处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("rawtypes")
@Component
public class OrmObjectingProcessor implements ModelBeforeComputeApi, ModelAfterComputeApi, PersistenceModelAfterComputeApi,
        FieldRecursionComputeApi {

    @Override
    public Object before(String model, Object obj) {
        return OrmProcessorHolder.objectingProcessor().before(model, obj);
    }

    @Override
    public Object after(String model, Object obj) {
        return OrmProcessorHolder.objectingProcessor().after(model, obj);
    }

    @Override
    public Object after(ModelConfig modelConfig, Object obj) {
        return OrmProcessorHolder.objectingProcessor().after(modelConfig, obj);
    }
}

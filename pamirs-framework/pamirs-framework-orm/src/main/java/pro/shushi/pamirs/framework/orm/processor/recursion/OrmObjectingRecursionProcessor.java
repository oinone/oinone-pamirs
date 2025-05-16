package pro.shushi.pamirs.framework.orm.processor.recursion;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.processor.OrmObjectingProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelRecursionComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;

/**
 * ORM对象化 递归处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class OrmObjectingRecursionProcessor extends OrmObjectingProcessor {

    @Override
    public void run(@SuppressWarnings("rawtypes") ModelRecursionComputeApi subModelComputeApi, ModelFieldConfig config, Map<String, Object> dMap) {
        TypeProcessor typeProcessor = Spider.getExtension(TypeProcessor.class, NamespaceConstants.spiDefault);
        if (typeProcessor.isRelationField(config.getTtype()) || typeProcessor.isRelationRelatedField(config.getTtype(), config.getRelatedTtype())) {
            Object value = dMap.get(config.getLname());
            if (null != value && !D.class.isAssignableFrom(value.getClass())) {
                //noinspection unchecked
                dMap.put(config.getLname(), subModelComputeApi.run(config.getReferences(), value));
            }
        }
    }

}

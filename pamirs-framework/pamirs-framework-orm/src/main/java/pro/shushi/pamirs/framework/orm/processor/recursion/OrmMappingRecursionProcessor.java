package pro.shushi.pamirs.framework.orm.processor.recursion;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.processor.OrmMappingProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelRecursionComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Map;

/**
 * ORM map化 递归处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class OrmMappingRecursionProcessor extends OrmMappingProcessor {

    @Override
    public void run(ModelRecursionComputeApi subModelComputeApi, ModelFieldConfig config, Map<String, Object> dMap) {
        TypeProcessor typeProcessor = Spider.getExtension(TypeProcessor.class, NamespaceConstants.spiDefault);
        if (typeProcessor.isRelationField(config.getTtype()) || typeProcessor.isRelationRelatedField(config.getTtype(), config.getRelatedTtype())) {
            Object value = dMap.get(config.getLname());
            if (null != value && !Map.class.isAssignableFrom(value.getClass())) {
                FieldUtils.setFieldValue(dMap, config.getLname(), subModelComputeApi.run(config.getReferences(), value));
            }
        }
    }

}

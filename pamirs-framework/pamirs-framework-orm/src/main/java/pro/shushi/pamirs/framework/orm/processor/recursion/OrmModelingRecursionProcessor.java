package pro.shushi.pamirs.framework.orm.processor.recursion;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.processor.OrmModelingProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelRecursionComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Map;

/**
 * ORM 模型化 递归处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class OrmModelingRecursionProcessor extends OrmModelingProcessor {

    @Override
    public void run(@SuppressWarnings("rawtypes") ModelRecursionComputeApi subModelComputeProcessor, ModelFieldConfig config, Map<String, Object> dMap) {
        TypeProcessor typeProcessor = Spider.getExtension(TypeProcessor.class, NamespaceConstants.spiDefault);
        if (typeProcessor.isRelationField(config.getTtype()) || typeProcessor.isRelationRelatedField(config.getTtype(), config.getRelatedTtype())) {
            Object value = dMap.get(config.getName());
            if (null != value) {
                String fieldModel = Models.api().getModel(dMap);
                if (StringUtils.isBlank(fieldModel)) {
                    //noinspection unchecked
                    subModelComputeProcessor.run(config.getReferences(), value);
                }
            }
        }
    }

}

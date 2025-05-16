package pro.shushi.pamirs.framework.orm.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 引用字段转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@SuppressWarnings("unused")
@Component
public class RelatedConvertProcessor {

    @Resource
    private RelatedFieldManager defaultRelatedFieldProcessor;

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
        defaultRelatedFieldProcessor.fillRelatedFieldValueFromRelation(fieldConfig, dMap);
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
//        defaultRelatedFieldProcessor.deSerializeStoreRelatedFieldValue(fieldConfig, dMap);
    }

}

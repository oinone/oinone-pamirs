package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.Map;

/**
 * 引用字段转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public interface RelatedFieldManager {

    void fillRelatedFieldValueFromRelation(ModelFieldConfig fieldConfig, Map<String, Object> dMap);

    void deSerializeStoreRelatedFieldValue(ModelFieldConfig fieldConfig, Map<String, Object> dMap);

}

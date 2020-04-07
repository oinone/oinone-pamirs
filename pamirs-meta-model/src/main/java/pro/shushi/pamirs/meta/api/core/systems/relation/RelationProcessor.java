package pro.shushi.pamirs.meta.api.core.systems.relation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.lang.reflect.Field;

/**
 * 关联关系系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface RelationProcessor extends CommonApi {

    /**
     * 通过关联关系字段获取默认关联模型编码
     *
     * @param field 关联关系字段
     * @return 如果字段是Map类型和基本类型，则返回null
     */
    String defaultReferenceFromField(Field field);

    /**
     * 生成缺省的关系字段
     *
     * @param modelDefinition
     * @param relation
     * @param relationFieldName
     * @param referenceField
     * @param source
     * @return
     */
    ModelField makeRelationField(ModelDefinition modelDefinition, ModelField relation, String relationFieldName,
                                 ModelField referenceField, SystemSourceEnum source);

}

package pro.shushi.pamirs.meta.api.core.compute.systems.relation;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

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
    @SuppressWarnings("unused")
    String defaultReferenceFromField(Field field);

    /**
     * 生成多对一关联关系字段
     *
     * @param model           关系模型
     * @param references      关联模型
     * @param newField        新字段名
     * @param relationFields  关系字段
     * @param referenceFields 关联字段
     * @return 多对一关联关系字段
     */
    @SuppressWarnings("unused")
    ModelField makeManyToOneField(ModelDefinition model, ModelDefinition references, String newField, List<String> relationFields, List<String> referenceFields);

    /**
     * 默认生成多对多模型关联关系字段
     *
     * @param context        上下文
     * @param relationModel  多对多关系模型
     * @param throughModel   多对多中间模型
     * @param referenceModel 多对多关联模型
     * @param relation       关联关系字段
     * @param newModel       新创建模型
     * @return 多对多模型关联关系字段
     */
    List<ModelField> makeManyToManyField(ComputeContext context, Meta meta,
                                         ModelDefinition relationModel, ModelDefinition throughModel, ModelDefinition referenceModel,
                                         ModelField relation, boolean newModel);

    /**
     * 补充默认的中间模型编码
     *
     * @param relation 关联关系字段
     */
    void makeDefaultThrough(ModelField relation);

    /**
     * 补充关联关系字段默认的中间模型关系字段和关联字段配置
     *
     * @param relationModel  多对多关系模型
     * @param referenceModel 多对多关联模型
     * @param relation       关联关系字段
     */
    void makeDefaultThroughRelationReferenceFields(ModelDefinition relationModel, ModelDefinition referenceModel, ModelField relation);

    /**
     * 为关联关系字段生成默认关系字段和关联字段
     *
     * @param meta     元数据
     * @param model    模型
     * @param relation 关联关系字段
     */
    void makeDefaultRelationReferenceFields(Meta meta, ModelDefinition model, ModelField relation);

    /**
     * 生成关系字段列表
     *
     * @param context  上下文
     * @param meta     元数据
     * @param model    模型
     * @param relation 关联关系字段
     */
    void makeRelationFields(ComputeContext context, Meta meta, ModelDefinition model, ModelField relation);

    /**
     * 生成关联字段列表
     *
     * @param context    上下文
     * @param meta       元数据
     * @param references 模型
     * @param relation   关联关系字段
     */
    void makeReferenceFields(ComputeContext context, Meta meta, ModelDefinition references, ModelField relation);

    /**
     * 生成缺省的关系字段
     *
     * @param context           上下文
     * @param modelDefinition   模型
     * @param relationFieldName 关系字段名称
     * @param relation          关联关系字段
     * @param required          是否必填
     * @param crossingExtend    跨模型扩展
     * @param sourceEnum        字段来源
     * @param relationField     已存在关系字段
     * @param referenceField    关联字段
     * @return 默认关系字段
     */
    ModelField makeRelationField(ComputeContext context, Meta meta,
                                 ModelDefinition modelDefinition, String relationFieldName, ModelField relation,
                                 boolean required, boolean crossingExtend, SystemSourceEnum sourceEnum,
                                 ModelField relationField, ModelField referenceField);

    /**
     * 生成缺省的引用字段
     *
     * @param modelDefinition   模型
     * @param relation          关联关系字段
     * @param relationFieldName 关系字段名称
     * @param sourceEnum        字段来源
     * @param referenceField    关联字段
     * @return 默认引用字段
     */
    @SuppressWarnings("unused")
    ModelField makeRelatedField(ModelDefinition modelDefinition, ModelField relation, String relationFieldName, SystemSourceEnum sourceEnum, ModelField referenceField);

    /**
     * 计算继承重写字段
     *
     * @param meta              元数据
     * @param modelField        字段
     * @param overrideFieldName 重写字段名称
     * @param consumer          顶级重写字段的消费者
     */
    void computeOverrideField(Meta meta, ModelField modelField, String overrideFieldName, BiConsumer<ModelDefinition, ModelField> consumer);

    /**
     * 重写字段
     *
     * @param meta               元数据
     * @param modelField         字段
     * @param overrideModelField 重写字段名称
     */
    void overrideField(Meta meta, ModelField modelField, ModelField overrideModelField);

}

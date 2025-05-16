package pro.shushi.pamirs.framework.compute.process.definition.field;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.framework.compute.process.definition.model.InheritedModelComputer;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.RelationProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.ThroughModelProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.base.BaseRelation;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 多对多关联关系字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class ManyToManyFieldComputer implements FieldComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, ModelDefinition data) {
        Result<Void> result = new Result<>();
        // 计算多对多模型
        if (field.getRelationStore() && TtypeEnum.M2M.value().equals(field.getTtype().value())) {
            RelationProcessor relationProcessor = CommonApiFactory.getApi(RelationProcessor.class);

            // 获取关系模型
            ModelDefinition relationModel = meta.getModel(field.getModel());
            // 获取关联模型
            ModelDefinition referenceModel = meta.getModel(field.getReferences());
            if (referenceModel == null) {
                throw PamirsException.construct(ComputeExpEnumerate.BASE_MODULE_DEPENDENT_OR_MODEL_CONFIG_ERROR)
                        .appendMsg("model: " + field.getModel() + ", field: " + field.getField() + ", references: " + field.getReferences()).errThrow();
            }
            if (ModelTypeEnum.TRANSIENT.value().equals(relationModel.getType().value())
                    || ModelTypeEnum.ABSTRACT.value().equals(relationModel.getType().value())
                    || ModelTypeEnum.TRANSIENT.value().equals(referenceModel.getType().value())
                    || ModelTypeEnum.ABSTRACT.value().equals(referenceModel.getType().value())
            ) {
                return result;
            }
            if (SystemSourceEnum.isInherited(field.getSystemSource())) {
                return result;
            }
            // 获取中间模型
            String through = field.getThrough();
            ModelDefinition throughModel = meta.getModel(through);
            boolean newCreate = false;
            if (null == throughModel) {
                //如果关联模型不存在，使用该方式直接创建
                throughModel = CommonApiFactory.getApi(ThroughModelProcessor.class).generate(context, meta, data, field);
                meta.addData(throughModel.getModule(), throughModel);
                newCreate = true;
            } else if (null == throughModel.getIsRelationship() || !throughModel.getIsRelationship()) {
                throw PamirsException.construct(ComputeExpEnumerate.BASE_THROUGH_MODEL_TYPE_ERROR)
                        .appendMsg("model:" + through).errThrow();
            } else {
                if (throughModel.isMetaCompleted()) {
                    throughModel.setSystemSource(SystemSourceEnum.RELATION);
                }
                if (!needDeal(throughModel)) {
                    return result;
                }
                //使用relation模型的走该方法
                throughModel = CommonApiFactory.getApi(ThroughModelProcessor.class)
                        .update(context, meta, data, field, throughModel);
            }

            /* 添加默认关系字段和关联字段*/
            // 获取中间模型关系字段与关联字段
            List<ModelField> newModelFieldList = relationProcessor
                    .makeManyToManyField(context, meta, relationModel, throughModel, referenceModel, field, newCreate);
            throughModel.getModelFields().addAll(newModelFieldList);

            // 获取中间模型关系字段
            List<String> throughRelationFields = field.getThroughRelationFields();
            // 获取中间模型关联字段
            List<String> throughReferenceFields = field.getThroughReferenceFields();
            // 设置中间模型主键
            List<String> uniqueFields = new ArrayList<>();
            uniqueFields.addAll(swap(throughRelationFields, throughReferenceFields, true));
            uniqueFields.addAll(swap(throughRelationFields, throughReferenceFields, false));
            boolean isPkId = null != throughModel.getSuperModels() && throughModel.getSuperModels().contains(IdRelation.MODEL_MODEL);
            if (isPkId) {
                throughModel.setPk(Lists.newArrayList(FieldConstants.ID));
                // 添加唯一索引
                String uniqueFieldString = StringUtils.join(uniqueFields, CharacterConstants.SEPARATOR_COMMA);
                List<String> uniques = throughModel.getUniques();
                if (null != uniques) {
                    if (!uniques.contains(uniqueFieldString)) {
                        uniques.add(uniqueFieldString);
                    }
                } else {
                    throughModel.setUniques(Lists.newArrayList(uniqueFieldString));
                }
            } else {
                throughModel.setPk(uniqueFields);
            }
            // 计算多对多模型的继承
            //noinspection unchecked
            Spider.getExtension(ModelComputer.class, InheritedModelComputer.SPI_NAME)
                    .compute(context, meta, ModelDefinition.MODEL_MODEL, throughModel, null);
        }
        return result;
    }

    public boolean needDeal(ModelDefinition throughModel) {
        return (null != throughModel.getSystemSource() && SystemSourceEnum.RELATION.equals(throughModel.getSystemSource()))
                ||
                (null != throughModel.getSuperModels()
                        &&
                        (throughModel.getSuperModels().contains(BaseRelation.MODEL_MODEL)
                                || throughModel.getSuperModels().contains(IdRelation.MODEL_MODEL)));
    }

    private List<String> swap(List<String> t1, List<String> t2, boolean inverse) {
        int i = 0;
        List<String> left = t1;
        List<String> right = t2;
        int t2mi = t2.size();
        for (String t : t1) {
            if (t2mi <= i) {
                break;
            }
            if (t.compareTo(t2.get(i)) > 0) {
                left = t2;
                right = t1;
                break;
            }
            i++;
        }
        return inverse ? left : right;
    }

}

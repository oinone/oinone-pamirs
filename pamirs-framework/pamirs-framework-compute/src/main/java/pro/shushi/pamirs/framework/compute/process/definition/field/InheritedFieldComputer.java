package pro.shushi.pamirs.framework.compute.process.definition.field;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * 继承字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class InheritedFieldComputer implements FieldComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, ModelDefinition data) {
        Result<Void> result = new Result<>();
        // 计算关联关系字段
        if (null == data.getRedundancy() || !data.getRedundancy()) {
            if (field.getRelationStore()) {
                if (TtypeEnum.O2O.value().equals(field.getTtype().value())
                        && SystemSourceEnum.MULTI_TABLE_INHERITED.value().equals(field.getSystemSource().value())) {
                    String references = field.getReferences();
                    ModelDefinition superModel = meta.getModel(references);
                    Spider.getDefaultExtension(InheritedProcessor.class)
                            .dealFieldForMultiTableInherited(meta, data, superModel, field);
                }
            }
        }
        return result;
    }

}

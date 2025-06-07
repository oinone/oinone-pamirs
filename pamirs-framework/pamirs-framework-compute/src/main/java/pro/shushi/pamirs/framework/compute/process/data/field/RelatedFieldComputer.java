package pro.shushi.pamirs.framework.compute.process.data.field;

import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelatedFieldManager;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;

/**
 * 引用字段计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class RelatedFieldComputer<T> implements FieldComputer<T> {

    @Override
    public Result<Void> compute(ComputeContext context, ModelFieldConfig field, T data) {
        CommonApiFactory.getApi(RelatedFieldManager.class).fillRelatedFieldValueFromRelation(field, ((D) data).get_d());
        return new Result<>();
    }

}

package pro.shushi.pamirs.framework.compute.process.definition.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.List;
import java.util.Map;

/**
 * 乐观锁计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class OptimisticLockerModelComputer implements ModelComputer<Meta, ModelDefinition> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext) {
        Result<Void> result = new Result<>();
        // 计算模型是否拥有乐观锁字段
        if (StringUtils.isBlank(data.getOptimisticLockerField())) {
            computeOptimisticLocker(data);
        } else {
            ModelField modelField = data.fetchModelField(data.getOptimisticLockerField());
            if (modelField == null) {
                data.setOptimisticLocker(null);
                data.setOptimisticLockerField(null);
                computeOptimisticLocker(data);
            } else {
                modelField.setOptimisticLocker(Boolean.TRUE);
            }
        }
        return result;
    }

    private void computeOptimisticLocker(ModelDefinition data) {
        List<ModelField> modelFieldList = data.getModelFields();
        if (CollectionUtils.isEmpty(modelFieldList)) {
            return;
        }
        for (ModelField modelField : modelFieldList) {
            Boolean optimisticLocker = modelField.getOptimisticLocker();
            if (optimisticLocker == null) {
                modelField.setOptimisticLocker(Boolean.FALSE);
            } else if (optimisticLocker) {
                data.setOptimisticLocker(modelField);
                data.setOptimisticLockerField(modelField.getField());
                break;
            }
        }
    }
}

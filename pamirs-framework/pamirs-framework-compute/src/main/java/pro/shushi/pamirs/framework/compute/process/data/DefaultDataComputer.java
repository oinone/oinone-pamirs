package pro.shushi.pamirs.framework.compute.process.data;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.DataComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order
@Component
@SPI.Service
public class DefaultDataComputer<T> implements DataComputer<List<T>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Result<Void> compute(ComputeContext context, String model, List<T> data, ModelComputer modelComputer, FieldComputer... fieldComputers) {
        Result<Void> result = new Result<>();
        List<T> dataList = new ArrayList<>(data);
        if (null != modelComputer) {
            for (T item : dataList) {
                if (item instanceof MetaBaseModel && ((MetaBaseModel) item).isMetaCompleted()) {
                    continue;
                }
                Result computeResult = modelComputer.compute(context, model, item);
                result.fill(computeResult);
            }
        }

        if (null != fieldComputers) {
            List<ModelFieldConfig> modelFieldList = PamirsSession.getContext().getModelConfig(model).getModelFieldConfigList();
            if (!CollectionUtils.isEmpty(modelFieldList)) {
                for (T item : dataList) {
                    if (item instanceof MetaBaseModel && ((MetaBaseModel) item).isMetaCompleted()) {
                        continue;
                    }
                    List<String> fieldList = new ArrayList<>();
                    for (ModelFieldConfig modelField : modelFieldList) {
                        fieldList.add(modelField.getField());
                    }
                    if (CollectionUtils.isEmpty(fieldList)) {
                        continue;
                    }
                    for (String field : fieldList) {
                        for (FieldComputer fieldComputer : fieldComputers) {
                            ModelFieldConfig modelField = PamirsSession.getContext().getModelField(model, field);
                            Result computeResult = fieldComputer.compute(context, modelField, item);
                            result.fill(computeResult);
                            if (!result.isSuccess()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}

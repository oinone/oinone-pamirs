package pro.shushi.pamirs.framework.compute;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.compute.process.data.DefaultDataComputer;
import pro.shushi.pamirs.framework.compute.process.data.field.*;
import pro.shushi.pamirs.framework.compute.process.data.model.CheckModelLifecycleComputer;
import pro.shushi.pamirs.framework.compute.process.data.model.ConstructComputer;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.PamirsDataComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.data.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.data.ModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.template.DataComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.util.ListUtils;

import java.util.List;
import java.util.Objects;

/**
 * pamirs数据计算实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SuppressWarnings({"unused", "rawtypes"})
@Component
public class DefaultPamirsDataComputer<T> implements PamirsDataComputer<List<T>> {

    protected static final ModelComputer CONSTRUCT_COMPUTER = new ConstructComputer();
    protected static final ModelComputer CHECK_MODEL_COMPUTER = new CheckModelLifecycleComputer();
    protected static final ModelComputer CHECK_MODEL_LIFECYCLE_COMPUTER = new CheckModelLifecycleComputer();

    protected static final FieldComputer DEFAULT_FIELD_COMPUTER = new DefaultFieldComputer();
    protected static final FieldComputer RELATED_FIELD_COMPUTER = new RelatedFieldComputer();
    protected static final FieldComputer RELATION_FIELD_COMPUTER = new RelationFieldComputer();
    protected static final FieldComputer CHECK_FIELD_COMPUTER = new CheckFieldComputer();
    protected static final FieldComputer CHECK_FIELD_LIFECYCLE_COMPUTER = new CheckFieldLifecycleComputer();

    @SuppressWarnings({"unchecked"})
    @Override
    public Result<Void> computeModel(ComputeContext context, String model, List<T> data) {
        DataComputer dataComputer = Objects.requireNonNull(CommonApiFactory
                .getApi(DataComputer.class, DefaultDataComputer.class.getName()));
        // 数据计算
        Result<Void> result = dataComputer.compute(context, model, data, CONSTRUCT_COMPUTER,
                DEFAULT_FIELD_COMPUTER, RELATED_FIELD_COMPUTER, RELATION_FIELD_COMPUTER, CHECK_FIELD_COMPUTER);
        return result.fill(dataComputer.compute(context, model, data, CHECK_MODEL_COMPUTER));
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Result<Void> computeModelInLifecycle(ComputeContext context, String model, List<T> data) {
        DataComputer dataComputer = Objects.requireNonNull(CommonApiFactory
                .getApi(DataComputer.class, DefaultDataComputer.class.getName()));
        // 数据计算
        Result<Void> result = dataComputer.compute(context, model, data, CONSTRUCT_COMPUTER,
                DEFAULT_FIELD_COMPUTER, RELATED_FIELD_COMPUTER, RELATION_FIELD_COMPUTER, CHECK_FIELD_LIFECYCLE_COMPUTER);
        return result.fill(dataComputer.compute(context, model, data, CHECK_MODEL_LIFECYCLE_COMPUTER));
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Result<Void> computeRelationField(ComputeContext context, String model, List<T> data) {
        // 数据计算
        return Objects.requireNonNull(CommonApiFactory.getApi(DataComputer.class, DefaultDataComputer.class.getName()))
                .compute(context, model, data, null, RELATION_FIELD_COMPUTER, CHECK_FIELD_COMPUTER);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Result<Void> check(ComputeContext context, String model, Object data) {
        List dataList = convertData(data);
        // 数据计算
        return Objects.requireNonNull(CommonApiFactory.getApi(DataComputer.class, DefaultDataComputer.class.getName()))
                .compute(context, model, dataList, CHECK_MODEL_COMPUTER, CHECK_FIELD_COMPUTER);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Result<Void> checkModel(ComputeContext context, String model, Object data) {
        List dataList = convertData(data);
        // 数据计算
        return Objects.requireNonNull(CommonApiFactory.getApi(DataComputer.class, DefaultDataComputer.class.getName()))
                .compute(context, model, dataList, CHECK_MODEL_COMPUTER);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Result<Void> checkField(ComputeContext context, String model, Object data) {
        List dataList = convertData(data);
        // 数据计算
        return Objects.requireNonNull(CommonApiFactory.getApi(DataComputer.class, DefaultDataComputer.class.getName()))
                .compute(context, model, dataList, null, CHECK_FIELD_COMPUTER);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Result<Void> checkField(ComputeContext context, ModelFieldConfig field, Object data) {
        // 数据计算
        return CHECK_FIELD_COMPUTER.compute(context, field, data);
    }

    private List convertData(Object data) {
        List dataList;
        if (data instanceof List) {
            dataList = (List) data;
        } else {
            dataList = ListUtils.asList(data);
        }
        return dataList;
    }

}

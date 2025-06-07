package pro.shushi.pamirs.framework.compute;

import pro.shushi.pamirs.framework.compute.process.definition.field.*;
import pro.shushi.pamirs.framework.compute.process.definition.model.*;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 计算模板
 * <p>
 * 2020/7/11 6:46 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ComputeTemplate {

    @SuppressWarnings("rawtypes")
    protected static final ModelComputer INHERITED_MODEL_COMPUTER = new InheritedModelComputer();

    @SuppressWarnings("rawtypes")
    protected static final ModelComputer OPTIMISTIC_LOCKER_MODEL_COMPUTER = new OptimisticLockerModelComputer();

    @SuppressWarnings("rawtypes")
    protected static final ModelComputer CONSTRUCT_COMPUTER = new ConstructComputer();

    @SuppressWarnings("rawtypes")
    protected static final ModelComputer CHECK_MODEL_COMPUTER = new CheckModelComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer DEFAULT_FIELD_COMPUTER = new DefaultFieldComputer<ModelDefinition>();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer INHERITED_FIELD_COMPUTER = new InheritedFieldComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer RELATION_FIELD_COMPUTER = new RelationFieldComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer MANY_TO_MANY_FIELD_COMPUTER = new ManyToManyFieldComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer RELATED_FIELD_COMPUTER = new RelatedFieldComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer FIELD_OPTIONS_COMPUTER = new FieldOptionsComputer();

    @SuppressWarnings("rawtypes")
    protected static final FieldComputer CHECK_FIELD_COMPUTER = new CheckFieldComputer();

    @SuppressWarnings("rawtypes")
    protected static final ModelComputer FUSE_MODEL_COMPUTER = new FuseModelComputer();

    protected static final ModelComputer<Meta, ModelField> CONSTRUCT_COMPUTER_FOR_FIELD = new ConstructComputer<>();
    protected static final FieldComputer<Meta, ModelField> DEFAULT_FIELD_COMPUTER_FOR_FIELD = new DefaultFieldComputer<>();
    protected static final FieldComputer<Meta, ModelField> CHECK_FIELD_COMPUTER_FOR_FIELD = new CheckFieldComputer<>();

}

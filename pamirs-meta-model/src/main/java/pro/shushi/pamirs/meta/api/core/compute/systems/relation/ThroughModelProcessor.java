package pro.shushi.pamirs.meta.api.core.compute.systems.relation;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

/**
 * 中间模型处理器
 * <p>
 * 2020/10/30 12:14 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ThroughModelProcessor {

    String DISPLAY_NAME_MIDDLE = "与";

    String DISPLAY_NAME_END = "的关系";

    ModelDefinition generate(ComputeContext context, Meta meta, ModelDefinition sourceModel, ModelField relation);

    ModelDefinition update(ComputeContext context, Meta meta, ModelDefinition sourceModel, ModelField relation, ModelDefinition throughModel);

}

package pro.shushi.pamirs.meta.dsl.handler;

import pro.shushi.pamirs.meta.dsl.definition.helper.LogicTransformHelper;
import pro.shushi.pamirs.meta.dsl.definition.node.Logic;
import pro.shushi.pamirs.meta.dsl.model.Process;

import java.util.List;

public class LogicTransformHander implements TransformHandler<Logic>{

    @Override
    public List<Process> transform(Logic definition) {
        return LogicTransformHelper.transform(definition);
    }

    public static TransformHandler get() {
        return new LogicTransformHander();
    }
}

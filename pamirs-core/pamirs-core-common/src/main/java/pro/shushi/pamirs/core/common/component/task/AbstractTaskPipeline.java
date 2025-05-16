package pro.shushi.pamirs.core.common.component.task;

import pro.shushi.pamirs.core.common.component.util.PamirsTaskExecuteHelper;
import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsPipeline;
import pro.shushi.pamirs.core.common.pipeline.extension.DefaultPamirsPipeline;
import pro.shushi.pamirs.core.common.task.PamirsTask;

/**
 * @author Adamancy Zhang on 2021-05-14 16:56
 */
public abstract class AbstractTaskPipeline<T extends PamirsTask, E extends PamirsExchange> extends DefaultPamirsPipeline<E> implements PamirsPipeline<E> {

    protected final T task;

    public AbstractTaskPipeline(T task, Feature... features) {
        super(features);
        this.task = task;
    }

    public AbstractTaskPipeline(String signature, T task, Feature... features) {
        super(signature, features);
        this.task = task;
    }

    @Override
    public E invoke(E exchange) {
        return PamirsTaskExecuteHelper.execute(task, exchange, super::invoke);
    }
}

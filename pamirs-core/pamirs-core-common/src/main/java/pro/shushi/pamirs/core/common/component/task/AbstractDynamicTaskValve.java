package pro.shushi.pamirs.core.common.component.task;

import pro.shushi.pamirs.core.common.component.util.PamirsTaskExecuteHelper;
import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsValve;
import pro.shushi.pamirs.core.common.task.PamirsTask;

import java.util.function.Function;

/**
 * <h>抽象动态任务执行阀</h>
 *
 * @author Adamancy Zhang on 2021-05-15 14:32
 */
public abstract class AbstractDynamicTaskValve<T extends PamirsTask, E extends PamirsExchange> implements PamirsValve<E> {

    private final Function<E, T> generator;

    public AbstractDynamicTaskValve(Function<E, T> generator) {
        this.generator = generator;
    }

    /**
     * 封装后的执行方法
     *
     * @param exchange 交换对象
     * @return 交换对象
     */
    protected abstract E execute(E exchange);

    @Override
    public E invoke(E exchange) {
        return PamirsTaskExecuteHelper.execute(generator.apply(exchange), exchange, this::execute);
    }
}

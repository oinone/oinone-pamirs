package pro.shushi.pamirs.core.common.component.task;

import pro.shushi.pamirs.core.common.component.util.PamirsTaskExecuteHelper;
import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsValve;
import pro.shushi.pamirs.core.common.task.PamirsTask;

/**
 * <h>抽象任务执行阀</h>
 * <p>
 * 1、一般情况下，对任务的操作仅限于操作当前任务进度
 * 2、推荐使用增量方式更新任务进度 {@link PamirsTask#incrementProgress(int)}
 * </p>
 *
 * @author Adamancy Zhang on 2021-05-10 10:48
 */
public abstract class AbstractTaskValve<T extends PamirsTask, E extends PamirsExchange> implements PamirsValve<E> {

    protected final T task;

    public AbstractTaskValve(T task) {
        this.task = task;
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
        return PamirsTaskExecuteHelper.execute(task, exchange, this::execute);
    }
}

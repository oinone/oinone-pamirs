package pro.shushi.pamirs.core.common.component.util;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.task.PamirsTask;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.function.Function;

/**
 * 标准任务执行帮助类
 *
 * @author Adamancy Zhang on 2021-05-15 14:37
 */
@Slf4j
public class PamirsTaskExecuteHelper {

    private PamirsTaskExecuteHelper() {
        //reject create object
    }

    public static <E extends PamirsExchange> E execute(PamirsTask task, E exchange, Function<E, E> function) {
        if (task == null) {
            exchange = function.apply(exchange);
        } else {
            String signature = task.signature();
            log.info("Task: {}-start", signature);
            task.start();
            exchange = function.apply(exchange);
            if (exchange.isInterrupted()) {
                if (!task.hasError()) {
                    task.error(exchange.getThrowable());
                }
            }
            task.finish();
            log.info("Task: {}-finish", signature);
        }
        return exchange;
    }
}

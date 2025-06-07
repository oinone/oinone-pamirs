package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.common.util.MetaBootCountDown;
import pro.shushi.pamirs.core.common.entry.Holder;
import pro.shushi.pamirs.framework.common.config.TtlAsyncTaskExecutor;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;

import java.util.concurrent.Executor;

/**
 * 生命周期执行器帮助类
 * <p>
 * 生命周期结束后无法正确执行，请严格在生命周期中使用
 * </p>
 *
 * @author Adamancy Zhang at 19:18 on 2023-12-12
 */
public class LifecycleExecutorHelper {

    private LifecycleExecutorHelper() {
        //reject create object
    }

    public static void execute(Executor executor, Runnable runnable) {
        if (executor == null) {
            runnable.run();
            return;
        }
        Holder<Boolean> isAwait = new Holder<>(false);
        if (StringUtils.isNotBlank(PamirsTenantSession.getTenant())) {
            isAwait.set(true);
            MetaBootCountDown.increment();
        }
        TtlAsyncTaskExecutor.getExecutorService().execute(() -> {
            if (isAwait.get()) {
                try {
                    runnable.run();
                } finally {
                    MetaBootCountDown.decrement();
                }
            } else {
                runnable.run();
            }
        });
    }
}

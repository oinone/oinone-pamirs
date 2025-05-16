package pro.shushi.pamirs.core.common.function;

import java.util.Map;

/**
 * 环绕运行器
 *
 * @author Adamancy Zhang at 17:45 on 2021-04-22
 */
public class AroundRunnable implements Runnable {

    private final Map<String, String> sessionContext;

    private final Runnable runnable;

    public AroundRunnable(Runnable runnable) {
        this.sessionContext = SessionHelper.generatorSession();
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            SessionHelper.clear();
            SessionHelper.fillSession(sessionContext);
            runnable.run();
        } finally {
            SessionHelper.clear();
        }
    }
}
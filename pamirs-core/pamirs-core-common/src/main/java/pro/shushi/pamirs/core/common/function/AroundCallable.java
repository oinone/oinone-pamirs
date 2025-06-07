package pro.shushi.pamirs.core.common.function;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Adamancy Zhang on 2021-04-22 17:56
 */
public class AroundCallable<V> implements Callable<V> {

    private final Map<String, String> sessionContext;

    private final Callable<V> callable;

    public AroundCallable(Callable<V> callable) {
        this.sessionContext = SessionHelper.generatorSession();
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        try {
            SessionHelper.clear();
            SessionHelper.fillSession(sessionContext);
            return callable.call();
        } finally {
            SessionHelper.clear();
        }
    }
}

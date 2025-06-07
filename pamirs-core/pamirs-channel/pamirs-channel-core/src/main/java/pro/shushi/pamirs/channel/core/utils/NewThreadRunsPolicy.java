package pro.shushi.pamirs.channel.core.utils;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * NewThreadRunsPolicy
 *
 * @author yakir on 2020/04/23 21:04.
 */
public class NewThreadRunsPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            final Thread t = new Thread(r, "Temporary task executor");
            t.start();
        } catch (Throwable e) {
            throw new RejectedExecutionException("Failed to start a new thread", e);
        }
    }
}
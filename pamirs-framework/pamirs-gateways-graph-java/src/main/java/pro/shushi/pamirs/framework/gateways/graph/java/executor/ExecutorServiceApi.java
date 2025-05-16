package pro.shushi.pamirs.framework.gateways.graph.java.executor;

import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 请求线程执行器
 * <p>
 * 2021/3/29 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ExecutorServiceApi {

    private final static ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(getThreadNum(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("p.s.p.gateways.graph.executor");
            return t;
        }
    }));

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static int getThreadNum() {
        return Math.max(16, Runtime.getRuntime().availableProcessors() * 3);
    }

}

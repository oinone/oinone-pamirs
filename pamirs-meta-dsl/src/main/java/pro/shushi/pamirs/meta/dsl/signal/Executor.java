package pro.shushi.pamirs.meta.dsl.signal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Executor {

	private static final int						CORE_POOL_SIZE		= 2;

	private static final int						MAX_POOL_SIZE		= 10;

	private static final int						KEEP_ALIVE_TIME		= 100;

	private static final TimeUnit					KEEP_ALIVE_UNIT		= TimeUnit.SECONDS;

	private static final BlockingQueue<Runnable>	WORK_QUEUE			= new LinkedBlockingQueue<Runnable>();

	private static final RejectedExecutionHandler	REJECTED_HANDLER	= new ThreadPoolExecutor.CallerRunsPolicy();

	private static ThreadPoolExecutor				executor;

	@SuppressWarnings("unused")
	private static final Executor					INSTANCE			= new Executor();

	private Executor() {
		init();
	}

	private static synchronized final void init() {

		executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_UNIT, WORK_QUEUE,
				REJECTED_HANDLER);
	}

	public static void execute(Runnable r) {
		executor.execute(r);
	}

	public static void shutdown() {
		executor.shutdown();
	}
}

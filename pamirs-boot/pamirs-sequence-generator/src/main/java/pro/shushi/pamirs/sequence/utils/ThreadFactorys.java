package pro.shushi.pamirs.sequence.utils;

import java.security.AccessController;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactorys
 *
 * @author yakir on 2020/04/08 16:44.
 */
public class ThreadFactorys {

    public static class UpdateThreadFactory implements ThreadFactory {

        private static final AtomicInteger THREA_NUM = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "leaf-segment-update" + THREA_NUM.getAndIncrement());
        }
    }

    public static class UpdateCacheFromDbFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = sun.misc.SharedSecrets.getJavaLangAccess().newThreadWithAcc(r, AccessController.getContext());
            t.setName("leaf-check-idCache");
            t.setDaemon(true);
            return t;
        }
    }
}

package pro.shushi.pamirs.middle.lock;

/**
 * 锁服务执行结果
 *
 * @author Adamancy Zhang at 09:36 on 2021-10-20
 */
public final class LockResult {

    private final String key;

    private final boolean success;

    private final Type type;

    private final Throwable throwable;

    private LockResult(String key, boolean success, Type type, Throwable throwable) {
        this.key = key;
        this.success = success;
        this.type = type;
        this.throwable = throwable;
    }

    public static LockResult success(String key) {
        return new LockResult(key, true, Type.NONE, null);
    }

    public static LockResult failure(String key, Type type, Throwable throwable) {
        return new LockResult(key, false, type, throwable);
    }

    public String getKey() {
        return key;
    }

    public boolean isSuccess() {
        return success;
    }

    public Type getType() {
        return type;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * 锁服务执行类型
     */
    public enum Type {

        /**
         * 无（当且仅当success为true时是该值）
         */
        NONE,

        /**
         * 获取锁
         */
        GET,

        /**
         * 申请锁
         */
        APPLY,

        /**
         * 消费锁
         */
        CONSUMER,

        /**
         * 释放锁
         */
        RELEASE
    }
}

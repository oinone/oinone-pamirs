package pro.shushi.pamirs.middle.lock;

/**
 * 锁消费者
 *
 * @author Adamancy Zhang at 12:46 on 2021-07-09
 */
@FunctionalInterface
public interface LockConsumer {

    /**
     * 执行后返回是否自动释放锁
     *
     * @return 是否自动释放锁
     */
    boolean accept();
}

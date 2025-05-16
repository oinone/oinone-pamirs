package pro.shushi.pamirs.core.common.task;

/**
 * <h>链式任务</h>
 * <p>
 * 链式任务是由标准任务{@link PamirsTask}衍生而来
 * </p>
 * <p>
 * 它类似与链表数据结构的基本能力，即拥有上一个任务和下一个任务的引用
 * </p>
 *
 * @author Adamancy Zhang on 2021-05-09 14:02
 */
public interface PamirsChainTask extends PamirsTask {

    /**
     * 获取上一个任务
     *
     * @return 上一个任务
     */
    PamirsTask getPrevious();

    /**
     * 设置上一个任务
     *
     * @param task 上一个任务
     */
    void setPrevious(PamirsTask task);

    /**
     * 设置上一个任务，不处理子任务
     *
     * @param task 上一个任务
     */
    void setRawPrevious(PamirsTask task);

    /**
     * 获取下一个任务
     *
     * @return 下一个任务
     */
    PamirsTask getNext();

    /**
     * 设置下一个任务
     *
     * @param task 下一个任务
     */
    void setNext(PamirsTask task);

    /**
     * 设置下一个任务，不处理子任务
     *
     * @param task 下一个任务
     */
    void setRawNext(PamirsTask task);
}

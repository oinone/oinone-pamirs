package pro.shushi.pamirs.core.common.task;

import java.util.List;

/**
 * <h>关联任务</h>
 * <p>
 * 关联任务是由标准任务{@link PamirsTask}衍生而来
 * </p>
 * <p>
 * 它类似于树形数据结构的基本能力，即拥有一个上级任务和多个子任务
 * </p>
 *
 * @author Adamancy Zhang on 2021-05-09 14:02
 */
public interface PamirsRelationTask extends PamirsTask {

    /**
     * 获取上级任务
     *
     * @return 上级任务
     */
    PamirsTask getParent();

    /**
     * 设置上级任务
     *
     * @param task 上级任务
     */
    void setParent(PamirsTask task);

    /**
     * 设置上级任务，不处理子任务
     *
     * @param rawTask 上级任务
     */
    void setRawParent(PamirsTask rawTask);

    /**
     * 获取子任务
     *
     * @return 子任务
     */
    List<PamirsTask> getChildren();

    /**
     * 添加子任务
     *
     * @param task 子任务
     */
    void addChild(PamirsTask task);

    /**
     * 添加子任务，不处理上级任务
     *
     * @param task 子任务
     */
    void addRawChild(PamirsTask task);

    /**
     * 移除子任务
     *
     * @param task 子任务
     */
    void removeChild(PamirsTask task);

    /**
     * 移除子任务，不处理上级任务
     *
     * @param task 子任务
     */
    void removeRawChild(PamirsTask task);
}

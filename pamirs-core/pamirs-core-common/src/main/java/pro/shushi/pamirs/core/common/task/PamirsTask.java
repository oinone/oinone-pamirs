package pro.shushi.pamirs.core.common.task;

import pro.shushi.pamirs.core.common.signature.PamirsSignature;

/**
 * <h>标准任务</h>
 * <p>
 * 由标准任务衍生出以下两种任务类型：<br/>
 * 1、关联任务 {@link PamirsRelationTask}<br/>
 * 2、链式任务 {@link PamirsChainTask}<br/>
 * </p>
 * <p>
 * 注：为了更好的描述任务间关系，所有任务类型的默认实现均不关心循环依赖问题，由使用者自行处理或限制<br/>
 * </p>
 *
 * @author Adamancy Zhang on 2021-05-09 15:07
 */
public interface PamirsTask extends PamirsSignature {

    /**
     * 是否开始
     *
     * @return 是否开始
     */
    boolean isStarted();

    /**
     * 是否完成
     *
     * @return 是否完成
     */
    boolean isFinished();

    /**
     * 是否正在运行
     *
     * @return 是否正在运行
     */
    boolean isRunning();

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    long getStartTime();

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    long getEndTime();

    /**
     * 任务开始
     */
    void start();

    /**
     * 任务完成
     */
    void finish();

    /**
     * 获取当前任务进度值
     *
     * @return 当前任务进度值
     */
    int getProgress();

    /**
     * 设置当前任务进度值为指定任务进度值
     *
     * @param value 指定任务进度值
     */
    void setProgress(int value);

    /**
     * 在当前任务进度值的基础上加上指定任务进度值
     *
     * @param value 指定任务进度值
     */
    void incrementProgress(int value);

    /**
     * 获取任务消息
     *
     * @return 任务消息
     */
    String getMessage();

    /**
     * 设置任务消息
     *
     * @param message 任务消息
     */
    void setMessage(String message);

    /**
     * 任务报错，提供错误信息
     *
     * @param errorMessage 错误信息
     */
    void error(String errorMessage);

    /**
     * 任务报错，提供异常
     *
     * @param throwable 异常
     */
    void error(Throwable throwable);

    /**
     * 任务报错，提供错误信息和异常
     *
     * @param errorMessage 错误信息
     * @param throwable    异常
     */
    void error(String errorMessage, Throwable throwable);

    /**
     * 是否有异常
     *
     * @return 是否有异常
     */
    boolean hasError();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getErrorMessage();

    /**
     * 获取任务异常
     *
     * @return 异常
     */
    Throwable getThrowable();
}

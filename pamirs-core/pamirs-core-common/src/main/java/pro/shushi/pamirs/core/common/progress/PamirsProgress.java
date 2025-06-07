package pro.shushi.pamirs.core.common.progress;

/**
 * 进度
 *
 * @author Adamancy Zhang on 2021-05-09 13:15
 */
public interface PamirsProgress {

    /**
     * 获取当前进度值
     *
     * @return 当前进度值
     */
    int get();

    /**
     * 设置当前进度值为指定进度值
     *
     * @param value 指定进度值
     */
    void set(int value);

    /**
     * 在当前进度值的基础上加上指定进度值
     *
     * @param value 指定进度值
     */
    void increment(int value);

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
     * 进度开始
     */
    void start();

    /**
     * 进度完成
     */
    void finish();
}

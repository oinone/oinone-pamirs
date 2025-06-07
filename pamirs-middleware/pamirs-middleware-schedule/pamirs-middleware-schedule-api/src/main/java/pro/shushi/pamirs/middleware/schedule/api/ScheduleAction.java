package pro.shushi.pamirs.middleware.schedule.api;


import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;

/**
 * 调度动作
 */
public interface ScheduleAction {

    String SEPARATOR_OCTOTHORPE = "#";

    String DEFAULT_METHOD = "execute";

    /**
     * <h>接口名称</h>
     * <p>
     * 请尽可能使用全类名，如全类名过长，请考虑其他可识别该执行任务的名称
     * </p>
     *
     * @return 接口名称
     */
    String getInterfaceName();

    /**
     * <h>调度任务执行逻辑</h>
     * <p>
     * 请在使用时添加Function注解
     * </p>
     *
     * @param task 调度任务
     * @return 执行结果
     */
    Result<Void> execute(ScheduleItem task);

    /**
     * 请尽可能不要重写该方法，因为你完全没有必要这样做
     *
     * @return 默认调度函数名称
     */
    default String getMethodName() {
        return DEFAULT_METHOD;
    }

    /**
     * 请尽可能不要重写该方法，因为你完全没有必要这样做
     *
     * @return 动作名称
     */
    default String getActionName() {
        return this.getInterfaceName() + SEPARATOR_OCTOTHORPE + this.getMethodName();
    }
}

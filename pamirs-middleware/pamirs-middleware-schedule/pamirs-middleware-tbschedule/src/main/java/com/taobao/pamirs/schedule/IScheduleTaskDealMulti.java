package com.taobao.pamirs.schedule;

/**
 * 可批处理的任务接口
 *
 * @author xuannan
 */
public interface IScheduleTaskDealMulti<T> extends IScheduleTaskDeal<T> {

    /**
     * 执行给定的任务数组。因为泛型不支持new 数组，只能传递OBJECT[]
     *
     * @param tasks 任务数组
     * @param ownSign 当前环境名称
     */
    boolean execute(T[] tasks, String ownSign) throws Exception;
}

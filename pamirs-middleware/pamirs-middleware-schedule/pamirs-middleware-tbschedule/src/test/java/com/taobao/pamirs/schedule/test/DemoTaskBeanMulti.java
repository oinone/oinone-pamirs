package com.taobao.pamirs.schedule.test;

import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TaskItemDefine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单个任务处理实现
 *
 * @author xuannan
 */
public class DemoTaskBeanMulti implements IScheduleTaskDealMulti<Long> {

    protected static transient Logger log = LoggerFactory.getLogger(DemoTaskBeanMulti.class);

    @Override
    public Comparator<Long> getComparator() {
        return new Comparator<Long>() {
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }

            public boolean equals(Object obj) {
                return this == obj;
            }
        };
    }

    @Override
    public List<Long> selectTasks(String taskParameter, String ownSign, int taskItemNum,
        List<TaskItemDefine> queryCondition, int fetchNum) {
        List<Long> result = new ArrayList<Long>();
        int num = fetchNum / queryCondition.size();
        Random random = new Random(System.currentTimeMillis());
        String message = "获取数据...[ownSign=" + ownSign + ",taskParameter=\"" + taskParameter + "\"]:";
        boolean isFirst = true;
        for (TaskItemDefine s : queryCondition) {
            long taskItem = Long.parseLong(s.getTaskItemId()) * 10000000L;
            for (int i = 0; i < num; i++) {
                result.add(taskItem + random.nextLong() % 100000L);
            }
            if (isFirst == false) {
                message = message + ",";
            } else {
                isFirst = false;
            }
            message = message + s.getTaskItemId() + "{" + s.getParameter() + "}";
        }
        log.info(message);
        return result;
    }

    @Override
    public boolean execute(Long[] tasks, String ownSign) throws Exception {
        Thread.sleep(50);
        log.info("处理任务[" + ownSign + "]:" + tasks);
        return true;
    }

}
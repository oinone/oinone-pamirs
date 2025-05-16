package com.taobao.pamirs.schedule.test;

import com.taobao.pamirs.schedule.ScheduleUtil;
import org.junit.Test;

public class ScheduleUtilTest {

    @Test
    public void TestAssignTaskNumber() {
        System.out.println(print(ScheduleUtil.assignTaskNumber(1, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(2, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(3, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(4, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(5, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(6, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(7, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(8, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(9, 10, 0)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(10, 10, 0)));

        System.out.println("-----------------");

        System.out.println(print(ScheduleUtil.assignTaskNumber(1, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(2, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(3, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(4, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(5, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(6, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(7, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(8, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(9, 10, 3)));
        System.out.println(print(ScheduleUtil.assignTaskNumber(10, 10, 3)));
    }

    private static String print(int[] items) {
        String s = "";
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                s = s + ",";
            }
            s = s + items[i];
        }
        return s;
    }
}

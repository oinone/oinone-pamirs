package pro.shushi.pamirs.middleware.schedule.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算当前时间使用的schedule表
 * <p/>
 * ———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
 * |星期 |     周日           |         周一       |        周二         |        周三        |         周四        |       周五           |       周六          |
 * ———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
 * |上午0| pamirs_schedule_0 | pamirs_schedule_2 |  pamirs_schedule_4  | pamirs_schedule_6 |  pamirs_schedule_8 |  pamirs_schedule_10 | pamirs_schedule_13 |
 * |——————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
 * |下午1| pamirs_schedule_1 | pamirs_schedule_3 |  pamirs_schedule_5  | pamirs_schedule_7 |  pamirs_schedule_9 |  pamirs_schedule_12 | pamirs_schedule_14 |
 * ———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————
 *
 * @version 1.0
 * @since 2016/11/2
 */
public class ScheduleTable {

    private static final Map<String, Integer> TABLE_MAPPING = new HashMap<>();

    static {
        TABLE_MAPPING.put("10", 0);
        TABLE_MAPPING.put("11", 1);
        TABLE_MAPPING.put("20", 2);
        TABLE_MAPPING.put("21", 3);
        TABLE_MAPPING.put("30", 4);
        TABLE_MAPPING.put("31", 5);
        TABLE_MAPPING.put("40", 6);
        TABLE_MAPPING.put("41", 7);
        TABLE_MAPPING.put("50", 8);
        TABLE_MAPPING.put("51", 9);
        TABLE_MAPPING.put("60", 10);
        TABLE_MAPPING.put("61", 11);
        TABLE_MAPPING.put("70", 12);
        TABLE_MAPPING.put("71", 13);
    }

    public static Integer getNum(int dayWeek, int ampm) {
        String key = dayWeek + Integer.toString(ampm);
        return TABLE_MAPPING.get(key);
    }
}

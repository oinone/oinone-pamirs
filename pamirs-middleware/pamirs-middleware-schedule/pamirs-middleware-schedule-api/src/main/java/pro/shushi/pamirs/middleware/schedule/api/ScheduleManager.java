package pro.shushi.pamirs.middleware.schedule.api;

import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 12:07
 */
public interface ScheduleManager {

    /**
     * 通过序号清空表
     *
     * @param tableNum 表序号
     */
    void deleteBySeparate(String tableNum);

    int updateTaskStatusById(ScheduleItem task);

    List<ScheduleItem> selectList(ScheduleQuery wrapper);

    int update2SucessByPrimaryKey(ScheduleItem task);

    List<ScheduleItem> selectDelayList(ScheduleQuery query);

    int update2TransferByPrimaryKey(ScheduleItem task);

    void insert(ScheduleItem task);

    void insertRecord(ScheduleItem task);

    List<ScheduleItem> selectListForSerial(ScheduleQuery query);

    int update2RetryByPrimaryKey(ScheduleItem task);

    int updateRetryCycleByPrimaryKey(ScheduleItem task);

    List<ScheduleItem> selectTypesListForSerial(ScheduleQuery query);
}

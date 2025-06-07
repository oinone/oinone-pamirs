package pro.shushi.pamirs.middleware.schedule.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleManager;
import pro.shushi.pamirs.middleware.schedule.core.dao.mapper.ScheduleItemMapper;
import pro.shushi.pamirs.middleware.schedule.core.dao.mapper.ScheduleItemTruncateMapper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-11-11 12:24
 */
@Component
public class ScheduleManagerImpl implements ScheduleManager {

    @Autowired
    private ScheduleItemMapper scheduleItemMapper;

    @Autowired
    private ScheduleItemTruncateMapper truncateMapper;

    @Override
    public void deleteBySeparate(String tableNum) {
        truncateMapper.deleteBySeparate(tableNum);
    }

    @Override
    public int updateTaskStatusById(ScheduleItem task) {
        return scheduleItemMapper.updateTaskStatusById(task);
    }

    @Override
    public List<ScheduleItem> selectList(ScheduleQuery wrapper) {
        return scheduleItemMapper.selectList(wrapper);
    }

    @Override
    public int update2SucessByPrimaryKey(ScheduleItem task) {
        return scheduleItemMapper.update2SucessByPrimaryKey(task);
    }

    @Override
    public List<ScheduleItem> selectDelayList(ScheduleQuery query) {
        return scheduleItemMapper.selectDelayList(query);
    }

    @Override
    public int update2TransferByPrimaryKey(ScheduleItem task) {
        return scheduleItemMapper.update2TransferByPrimaryKey(task);
    }

    @Override
    public void insert(ScheduleItem task) {
        scheduleItemMapper.insert(task);
    }

    @Override
    public void insertRecord(ScheduleItem task) {
        scheduleItemMapper.insertRecord(task);
    }

    @Override
    public List<ScheduleItem> selectListForSerial(ScheduleQuery query) {
        return scheduleItemMapper.selectListForSerial(query);
    }

    @Override
    public int update2RetryByPrimaryKey(ScheduleItem task) {
        return scheduleItemMapper.update2RetryByPrimaryKey(task);
    }

    @Override
    public int updateRetryCycleByPrimaryKey(ScheduleItem task) {
        return scheduleItemMapper.updateRetryCycleByPrimaryKey(task);
    }

    @Override
    public List<ScheduleItem> selectTypesListForSerial(ScheduleQuery query) {
        return scheduleItemMapper.selectTypesListForSerial(query);
    }
}

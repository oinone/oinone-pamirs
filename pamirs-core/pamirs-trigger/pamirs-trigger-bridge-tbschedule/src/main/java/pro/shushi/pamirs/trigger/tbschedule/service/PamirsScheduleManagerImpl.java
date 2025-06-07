package pro.shushi.pamirs.trigger.tbschedule.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.meta.annotation.sys.Ds;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleManager;
import pro.shushi.pamirs.middleware.schedule.core.service.ScheduleManagerImpl;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleQuery;
import pro.shushi.pamirs.trigger.tbschedule.model.PamirsSchedule;

import java.util.List;

/**
 * @author Adamancy Zhang on 2021-03-03 21:57
 */
@Ds(model = PamirsSchedule.MODEL_MODEL)
@Primary
@Service
public class PamirsScheduleManagerImpl extends ScheduleManagerImpl implements ScheduleManager {

    @Override
    public void deleteBySeparate(String tableNum) {
        super.deleteBySeparate(tableNum);
    }

    @Override
    public int updateTaskStatusById(ScheduleItem task) {
        return super.updateTaskStatusById(task);
    }

    @Override
    public List<ScheduleItem> selectList(ScheduleQuery wrapper) {
        return super.selectList(wrapper);
    }

    @Override
    public int update2SucessByPrimaryKey(ScheduleItem task) {
        return super.update2SucessByPrimaryKey(task);
    }

    @Override
    public List<ScheduleItem> selectDelayList(ScheduleQuery query) {
        return super.selectDelayList(query);
    }

    @Override
    public int update2TransferByPrimaryKey(ScheduleItem task) {
        return super.update2TransferByPrimaryKey(task);
    }

    @Override
    public void insert(ScheduleItem task) {
        super.insert(task);
    }

    @Override
    public void insertRecord(ScheduleItem task) {
        super.insertRecord(task);
    }

    @Override
    public List<ScheduleItem> selectListForSerial(ScheduleQuery query) {
        return super.selectListForSerial(query);
    }

    @Override
    public int update2RetryByPrimaryKey(ScheduleItem task) {
        return super.update2RetryByPrimaryKey(task);
    }

    @Override
    public int updateRetryCycleByPrimaryKey(ScheduleItem task) {
        return super.updateRetryCycleByPrimaryKey(task);
    }

    @Override
    public List<ScheduleItem> selectTypesListForSerial(ScheduleQuery query) {
        return super.selectTypesListForSerial(query);
    }
}

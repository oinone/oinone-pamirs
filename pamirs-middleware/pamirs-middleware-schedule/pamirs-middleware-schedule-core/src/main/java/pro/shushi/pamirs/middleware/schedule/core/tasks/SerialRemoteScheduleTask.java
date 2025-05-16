package pro.shushi.pamirs.middleware.schedule.core.tasks;

import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.function.FunctionService;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleItem;
import pro.shushi.pamirs.middleware.schedule.spi.ScheduleRemoteTaskFunctionApi;

import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2020-12-25 20:50
 */
public class SerialRemoteScheduleTask extends AbstractSerialScheduleTaskDealSingle implements IScheduleTaskDealSingle<List<ScheduleItem>> {

    @Autowired
    private FunctionService functionService;

    @Override
    protected Result<Void> execute0(ScheduleItem task, String ownSign) {
        ScheduleItem remoteTask = Spider.getDefaultExtension(ScheduleRemoteTaskFunctionApi.class).handleBeforeExecuteFun(task);
        return functionService.execute(getFunctionDefinition(remoteTask), task);
    }
}

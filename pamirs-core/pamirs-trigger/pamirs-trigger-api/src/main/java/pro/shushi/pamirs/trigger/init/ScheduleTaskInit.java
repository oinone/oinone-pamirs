package pro.shushi.pamirs.trigger.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
import pro.shushi.pamirs.trigger.service.ScheduleTaskActionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adamancy Zhang at 14:21 on 2025-04-27
 */
@Component
public class ScheduleTaskInit implements SystemBootAfterInit {

    @Autowired(required = false)
    private ScheduleTaskActionService scheduleTaskService;

    private static List<ScheduleTaskAction> initActions = new ArrayList<>();

    public static void addScheduleAction(ScheduleTaskAction action) {
        initActions.add(action);
    }

    @Override
    public boolean init(AppLifecycleCommand command) {
        if (scheduleTaskService == null || initActions.isEmpty()) {
            return true;
        }
        for (ScheduleTaskAction action : initActions) {
            scheduleTaskService.submit(action);
        }
        initActions.clear();
        initActions = null;
        return true;
    }

    @Override
    public int priority() {
        return 0;
    }
}

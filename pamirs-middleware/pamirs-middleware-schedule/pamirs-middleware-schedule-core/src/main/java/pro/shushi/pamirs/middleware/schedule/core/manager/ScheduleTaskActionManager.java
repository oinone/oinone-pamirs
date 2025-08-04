package pro.shushi.pamirs.middleware.schedule.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.api.ScheduleAction;
import pro.shushi.pamirs.middleware.schedule.core.condition.ScheduleSwitchCondition;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Conditional(ScheduleSwitchCondition.class)
public class ScheduleTaskActionManager {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskActionManager.class);

    private final Map<String, List<ScheduleAction>> taskActions = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() throws Exception {
        String[] actionNames = context.getBeanNamesForType(ScheduleAction.class, true, false);
        if (actionNames.length > 0) {
            for (String actionName : actionNames) {
                ScheduleAction action = context.getBean(actionName, ScheduleAction.class);
                taskActions.computeIfAbsent(action.getActionName(), k -> new ArrayList<>()).add(action);
            }
        } else {
            log.warn("No TaskAction is defined in the spring container");
        }
    }

    public List<ScheduleAction> getTaskAction(String actionName) {
        return taskActions.get(actionName);
    }

    public Map<String, List<ScheduleAction>> getActions() {
        return taskActions;
    }

}
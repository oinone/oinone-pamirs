package pro.shushi.pamirs.middleware.schedule.core.conf;

import com.alibaba.fastjson.JSONObject;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;

import java.util.Map;

public class TaskNodeConfiguration {

    private TaskType taskType;

    private String beanClassName;

    private String beanNames;

    private Map<String, Object> values = new JSONObject();

    private Map<String, Object> strategy = new JSONObject();

    public TaskNodeConfiguration(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public TaskNodeConfiguration setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        return this;
    }

    public String getBeanNames() {
        return beanNames;
    }

    public TaskNodeConfiguration setBeanNames(String beanNames) {
        this.beanNames = beanNames;
        return this;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public TaskNodeConfiguration setValues(Map<String, Object> values) {
        this.values = values;
        return this;
    }

    public Map<String, Object> getStrategy() {
        return strategy;
    }

    public TaskNodeConfiguration setStrategy(Map<String, Object> strategy) {
        this.strategy = strategy;
        return this;
    }
}

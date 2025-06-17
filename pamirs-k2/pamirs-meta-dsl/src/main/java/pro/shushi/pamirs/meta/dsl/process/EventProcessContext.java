package pro.shushi.pamirs.meta.dsl.process;

import java.util.List;
import java.util.Map;

public class EventProcessContext {

    public String processName;

    public Integer processVersion;

    public String executeEvent;

    public List<String> executedPath;

    public List<String> nextEvents;

    public Map<String, Object> context;

    public EventProcessContext() {}

    public void init(String processName, Integer processVersion, String executeEvent,
                     Map<String, Object> context) {
        this.processName = processName;
        this.processVersion = processVersion;
        this.executeEvent = executeEvent;
        this.context = context;
    }

    public void init(String processName, Integer processVersion, String executeEvent,
                     List<String> executedPath, Map<String, Object> context) {
        this.processName = processName;
        this.processVersion = processVersion;
        this.executeEvent = executeEvent;
        this.executedPath = executedPath;
        this.context = context;
    }


}

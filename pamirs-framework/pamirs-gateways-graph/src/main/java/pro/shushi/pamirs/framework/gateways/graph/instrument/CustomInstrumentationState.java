package pro.shushi.pamirs.framework.gateways.graph.instrument;

import graphql.execution.instrumentation.InstrumentationState;

import java.util.HashMap;
import java.util.Map;

class CustomInstrumentationState implements InstrumentationState {
    private Map<String, Object> executeTimeStateMap = new HashMap<>();

    void recordTiming(String key, long time) {
        executeTimeStateMap.put(key, time);
    }
}

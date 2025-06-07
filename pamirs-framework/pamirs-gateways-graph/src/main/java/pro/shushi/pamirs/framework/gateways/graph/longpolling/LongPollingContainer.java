package pro.shushi.pamirs.framework.gateways.graph.longpolling;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LongPollingContainer implements LongPollingConstants {

    public final static Map<String/*key*/, Map<String/*traceId*/, PamirsLongPolling>> resultMap = new ConcurrentHashMap<>();

    public static String getKey(String type, String key) {
        if (StringUtils.isBlank(type)) {
            type = defaultType;
        }
        return new StringBuilder(type).append("_").append(key).toString();
    }

    public static void add(String key, String requestId, PamirsLongPolling pamirsLongPolling) {
        Map<String, PamirsLongPolling> pamirsLongPollingResultMap = resultMap.get(key);
        if (MapUtils.isEmpty(pamirsLongPollingResultMap)) {
            pamirsLongPollingResultMap = new ConcurrentHashMap<>();
        }
        pamirsLongPollingResultMap.put(requestId, pamirsLongPolling);
        resultMap.put(key, pamirsLongPollingResultMap);
    }

    public static PamirsLongPolling get(String type, String key, String traceId) {
        String uniqueKey = getKey(type, key);
        Map<String, PamirsLongPolling> pamirsLongPollingResultMap = resultMap.get(uniqueKey);
        if (MapUtils.isNotEmpty(pamirsLongPollingResultMap)) {
            return pamirsLongPollingResultMap.get(traceId);
        }
        return null;
    }

    public static List<PamirsLongPolling> getAll(String type, String key) {
        List<PamirsLongPolling> list = new ArrayList<>();
        String uniqueKey = getKey(type, key);
        Map<String, PamirsLongPolling> pamirsLongPollingResultMap = resultMap.get(uniqueKey);
        if (MapUtils.isNotEmpty(pamirsLongPollingResultMap)) {
            for (String field : pamirsLongPollingResultMap.keySet()) {
                PamirsLongPolling pamirsLongPolling = pamirsLongPollingResultMap.get(field);
                if (!ObjectUtils.isEmpty(pamirsLongPolling)) {
                    list.add(pamirsLongPolling);
                }
            }
        }
        return list;
    }

    public static String getTraceId() {
        Map<String, Object> variables = PamirsSession.getRequestVariables().getVariables();
        String s = UUID.randomUUID().toString();
        if (MapUtils.isEmpty(variables)) {
            variables = new HashMap<>();
            PamirsSession.getRequestVariables().setVariables(variables);
            variables.put("traceId", s);
        } else {
            String traceId = (String) variables.get("traceId");
            if (StringUtils.isBlank(traceId)) {
                variables.put("traceId", s);
            }
        }
        return (String) PamirsSession.getRequestVariables().getVariables().get("traceId");
    }

    public static void remove(String key, String traceId) {
        Map<String, PamirsLongPolling> pamirsLongPollingResultMap = resultMap.get(key);
        if (MapUtils.isNotEmpty(pamirsLongPollingResultMap)) {
            pamirsLongPollingResultMap.remove(traceId);
        }
        if (MapUtils.isEmpty(pamirsLongPollingResultMap)) {
            resultMap.remove(key);
        }
    }
}

package pro.shushi.pamirs.trigger.notify;

import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.trigger.model.NotifyDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotifyContext {

    private static final Map<String, Map<String, Map<String, NotifyDefinition>>> notifyDefinitionContext = new ConcurrentHashMap<>();

    public static void putNotifyDefinition(String functionNamespace, String functionFun, NotifyDefinition notifyDefinition) {
        Map<String, Map<String, NotifyDefinition>> tenantNotifyDefinitionMap = notifyDefinitionContext.computeIfAbsent(getCurrentTriggerLocal(), triggerLocal -> new ConcurrentHashMap<>());
        Map<String, NotifyDefinition> notifyDefinitionMap = tenantNotifyDefinitionMap.computeIfAbsent(functionNamespace, _functionNamespace -> new ConcurrentHashMap<>());
        NotifyDefinition target = notifyDefinitionMap.get(functionFun);
        if (target == null)
            notifyDefinitionMap.put(functionFun, notifyDefinition);
        else
            target.setTargetFunctionList(notifyDefinition.getTargetFunctionList());//此处直接替换原有的目标函数列表
    }

    public static NotifyDefinition getNotifyDefinition(String functionNamespace, String functionFun) {
        Map<String, Map<String, NotifyDefinition>> tenantNotifyDefinitionMap = notifyDefinitionContext.get(getCurrentTriggerLocal());
        if (tenantNotifyDefinitionMap == null) {
            return null;
        }
        Map<String, NotifyDefinition> notifyDefinitionMap = tenantNotifyDefinitionMap.get(functionNamespace);
        if (notifyDefinitionMap == null) {
            return null;
        }
        return notifyDefinitionMap.get(functionFun);
    }

    //该操作暂时不开放
//    public static void removeNotifyDefinition(String functionNamespace, String functionFun, String targetFunctionNamespace, String targetFunctionFun) {
//        Map<String, Map<String, NotifyDefinition>> tenantNotifyDefinitionMap = notifyDefinitionContext.get(getCurrentTriggerLocal());
//        if (tenantNotifyDefinitionMap == null)
//            return;
//        Map<String, NotifyDefinition> notifyDefinitionMap = tenantNotifyDefinitionMap.get(functionNamespace);
//        if (notifyDefinitionMap == null)
//            return;
//        NotifyDefinition notifyDefinition = notifyDefinitionMap.get(functionFun);
//        if (notifyDefinition == null)
//            return;
//        List<NotifyFunctionDefinition> targetFunctionList = notifyDefinition.getTargetFunctionList();
//        int index = 0;
//        for (NotifyFunctionDefinition item : targetFunctionList) {
//            if (targetFunctionNamespace.equals(item.getTargetNamespace()) && targetFunctionFun.equals(item.getTargetFun()))
//                break;
//            index++;
//        }
//        if (index != targetFunctionList.size())
//            notifyDefinition.getTargetFunctionList().remove(index);
//    }

    public static void removeNotifyDefinition(String functionNamespace, String functionFun) {
        Map<String, Map<String, NotifyDefinition>> tenantNotifyDefinitionMap = notifyDefinitionContext.get(getCurrentTriggerLocal());
        if (tenantNotifyDefinitionMap == null)
            return;
        Map<String, NotifyDefinition> notifyDefinitionMap = tenantNotifyDefinitionMap.get(functionNamespace);
        if (notifyDefinitionMap == null)
            return;
        notifyDefinitionMap.remove(functionFun);
    }

    public static void clear() {
        Map<String, Map<String, NotifyDefinition>> tenantNotifyDefinitionMap = notifyDefinitionContext.get(getCurrentTriggerLocal());
        if (tenantNotifyDefinitionMap == null)
            return;
        tenantNotifyDefinitionMap.clear();
    }

    private static String getCurrentTriggerLocal() {
        return PamirsTenantSession.getTenant() + ":" + PamirsTenantSession.isPreview();
    }
}

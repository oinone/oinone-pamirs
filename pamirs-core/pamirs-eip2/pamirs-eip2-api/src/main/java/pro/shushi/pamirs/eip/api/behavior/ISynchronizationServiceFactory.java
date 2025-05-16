package pro.shushi.pamirs.eip.api.behavior;

import com.google.api.client.util.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfo;
import pro.shushi.pamirs.eip.api.behavior.model.SynchronizationInfoDefinition;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步状态工厂
 */
public class ISynchronizationServiceFactory {

    private static Map<String, Map<String, ISynchronizationService>> CACHE;

    public static List<ISynchronizationService> getSyncService(String modelModel) {
        if (StringUtils.isBlank(modelModel)) {
            return Lists.newArrayList();
        }
        List<ISynchronizationService> result = new ArrayList<>();
        List<SynchronizationInfoDefinition> definitions = new SynchronizationInfoDefinition().setModelModel(modelModel)
                .setDataStatus(DataStatusEnum.ENABLED).queryList();
        if (CollectionUtils.isNotEmpty(definitions)) {
            Map<String, ISynchronizationService> serviceMap = init().get(modelModel);
            if (null != serviceMap) {
                for (SynchronizationInfoDefinition definition : definitions) {
                    if (serviceMap.containsKey(definition.getInterfaceName())) {
                        result.add(serviceMap.get(definition.getInterfaceName()));
                    }
                }
            }
        }
        return result;
    }

    public static ISynchronizationService getSyncService(String modelModel, String interfaceName) {
        List<SynchronizationInfoDefinition> definitions = new SynchronizationInfoDefinition()
                .setModelModel(modelModel).setInterfaceName(interfaceName).setDataStatus(DataStatusEnum.ENABLED).queryList();
        Map<String, ISynchronizationService> serviceMap = init().get(modelModel);
        return serviceMap != null && CollectionUtils.isNotEmpty(definitions) ? serviceMap.get(interfaceName) : null;
    }

    private static Map<String, Map<String, ISynchronizationService>> init() {
        if (CACHE == null) {
            synchronized (ISynchronizationServiceFactory.class) {
                if (CACHE == null) {
                    Map<String, ISynchronizationService> syncTradeServices = BeanDefinitionUtils.getBeansOfType(ISynchronizationService.class);
                    Map<String, Map<String, ISynchronizationService>> syncServiceMap = new HashMap<>();
                    for (ISynchronizationService service : syncTradeServices.values()) {
                        SynchronizationInfo synchronizationInfo = service.synchronizationInfo();
                        if (null == synchronizationInfo || null == synchronizationInfo.getModelModel()) {
                            continue;
                        }
                        String modelModel = synchronizationInfo.getModelModel();
                        Map<String, ISynchronizationService> serviceMap;
                        if (syncServiceMap.containsKey(modelModel)) {
                            serviceMap = syncServiceMap.get(modelModel);
                        } else {
                            serviceMap = new HashMap<>();
                        }
                        serviceMap.put(synchronizationInfo.getInterfaceName(), service);
                        syncServiceMap.put(modelModel, serviceMap);
                    }
                    CACHE = syncServiceMap;
                }
            }
        }
        return CACHE;
    }
}
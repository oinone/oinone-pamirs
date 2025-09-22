package pro.shushi.pamirs.eip.api.service.distribution;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

@Slf4j
@Component
public class EipZookeeperNodeListener implements TreeCacheListener {

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private EipInterfaceService interfaceService;

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
                addInterface(treeCacheEvent.getData());
                break;
            case NODE_UPDATED:
                updateInterface(treeCacheEvent.getData());
                break;
            case NODE_REMOVED:
                removeInterface(treeCacheEvent.getData());
                break;
        }
    }

    private void registerConsumer(InterfaceTypeEnum interfaceType, IEipApi eipApi, boolean isEnabled) {
        if (!isEnabled) {
            return;
        }
        switch (interfaceType) {
            case INTEGRATION:
                interfaceService.registerInterface((EipIntegrationInterface) eipApi);
                break;
            case OPEN:
                interfaceService.registerOpenInterface((EipOpenInterface) eipApi);
                break;
            case ROUTE:
                interfaceService.registerRouteDefinition((EipRouteDefinition) eipApi);
                break;
        }
    }

    private void cancellationConsumer(InterfaceTypeEnum interfaceType, IEipApi eipApi, boolean isEnabled) {
        if (isEnabled) {
            return;
        }
        switch (interfaceType) {
            case INTEGRATION:
                interfaceService.cancellationInterface((EipIntegrationInterface) eipApi);
                break;
            case OPEN:
                interfaceService.cancellationOpenInterface((EipOpenInterface) eipApi);
                break;
            case ROUTE:
                interfaceService.cancellationRouteDefinition((EipRouteDefinition) eipApi);
                break;
        }
    }

    private void addInterface(ChildData childData) {
        processInterfaceModify(childData, this::registerConsumer);
    }

    private void updateInterface(ChildData childData) {
        processInterfaceModify(childData, (interfaceType, eipApi, isEnable) -> {
            if (isEnable) {
                registerConsumer(interfaceType, eipApi, Boolean.TRUE);
            } else {
                cancellationConsumer(interfaceType, eipApi, Boolean.FALSE);
            }
        });
    }

    private void removeInterface(ChildData childData) {
        processInterfaceModify(childData, this::cancellationConsumer);
    }

    private void processInterfaceModify(ChildData data, EipInterfaceModifyProcessor consumer) {
        String path = data.getPath();
        int rootPathLength = zookeeperService.getRootPath().length() + EipDistributionSupport.NODE_PATH_PREFIX.length() + 1;
        if (path.length() <= rootPathLength) {
            return;
        }
        path = path.substring(rootPathLength);
        String[] pathList = path.split(CharacterConstants.SEPARATOR_SLASH);
        String tenant;
        if (pathList.length == 3) {
            tenant = pathList[0];
            pathList = new String[]{pathList[1], pathList[2]};
        } else if (pathList.length == 2) {
            tenant = null;
        } else {
            return;
        }
        if (tenant != null) {
            PamirsTenantSession.setTenant(tenant);
        }
        processInterfaceModify(data, pathList, consumer);
    }

    private void processInterfaceModify(ChildData childData, String[] pathList, EipInterfaceModifyProcessor consumer) {
        InterfaceTypeEnum interfaceType = InterfaceTypeEnum.safeValueOf(pathList[0]);
        if (interfaceType == null) {
            //无效类型忽略
            return;
        }
        byte[] data = childData.getData();
        Boolean isEnable = null;
        boolean isNotNull = false;
        if (data != null && data.length >= 1) {
            //此处仅处理有效数据变更
            byte data0 = data[0];
            if (data0 == EipDistributionSupport.ENABLED[0]) {
                isEnable = Boolean.TRUE;
            } else if (data0 == EipDistributionSupport.DISABLED[0]) {
                isEnable = Boolean.FALSE;
                isNotNull = true;
            }
        }
        if (isEnable != null) {
            IEipApi eipApi = fetchInterface(interfaceType, pathList[1], isNotNull);
            if (eipApi == null) {
                //无效接口信息忽略
                return;
            }
            //当有效数据变更时调用指定处理逻辑
            consumer.accept(interfaceType, eipApi, isEnable);
        }
    }

    private IEipApi fetchInterface(InterfaceTypeEnum interfaceType, String interfaceName, boolean isNotNull) {
        switch (interfaceType) {
            case INTEGRATION: {
                IEipApi eipApi = Models.origin().queryOne(new EipIntegrationInterface().setInterfaceName(interfaceName));
                if (eipApi == null && isNotNull) {
                    eipApi = new EipIntegrationInterface().setInterfaceName(interfaceName);
                }
                return eipApi;
            }
            case OPEN: {
                IEipApi eipApi = Models.origin().queryOne(new EipOpenInterface().setInterfaceName(interfaceName));
                if (eipApi == null && isNotNull) {
                    eipApi = new EipOpenInterface().setInterfaceName(interfaceName);
                }
                return eipApi;
            }
            case ROUTE: {
                IEipApi eipApi = Models.origin().queryOne(new EipRouteDefinition().setInterfaceName(interfaceName));
                if (eipApi == null && isNotNull) {
                    eipApi = new EipRouteDefinition().setInterfaceName(interfaceName);
                }
                return eipApi;
            }
            default:
                return null;
        }
    }

    @FunctionalInterface
    private interface EipInterfaceModifyProcessor {

        void accept(InterfaceTypeEnum interfaceType, IEipApi eipApi, Boolean isEnable);
    }
}

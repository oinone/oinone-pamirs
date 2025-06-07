package pro.shushi.pamirs.eip.view;

import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenu;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxMenus;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.pmodel.*;

/**
 * 可以注解到该模块的任意类上，建议同一个模块中只配置一处
 */
@UxMenus
class EipMenus {

    @UxMenu("集成管理")
    class EipMenu {
        @UxMenu("集成接口")
        @UxRoute(model = EipIntegrationInterface.MODEL_MODEL, module = EipModule.MODULE_MODULE)
        class EipIntegrationInterfaceMenu {
        }

        @UxMenu("熔断配置")
        @UxRoute(model = EipCircuitBreakerRuleProxy.MODEL_MODEL, module = EipModule.MODULE_MODULE)
        class EipCircuitBreakerRuleProxyMenu {
        }

        @UxMenu("熔断记录")
        @UxRoute(model = CircuitBreakerRecordProxy.MODEL_MODEL, module = EipModule.MODULE_MODULE)
        class CircuitBreakerRecordProxyMenu {
        }
    }

    @UxMenu("开放管理")
    class EipOpenMenu {
        @UxMenu("开放接口")
        @UxRoute(model = EipOpenInterface.MODEL_MODEL, viewName = "开放接口table")
        class EipOpenInterfaceMenu {
        }

        @UxMenu("应用")
        @UxRoute(model = EipApplicationProxy.MODEL_MODEL, viewName = "集成应用table")
        class EipApplicationProxyMenu {
        }

        @UxMenu("黑名单")
        @UxRoute(model = EipOpenIpBlacklistProxy.MODEL_MODEL, viewName = "开放应用黑名单table")
        class EipOpenIpBlacklistProxyMenu {
        }
    }

    /**
     * @UxMenu("场景管理") class EipSceneMenu {
     * @UxMenu("场景") @UxRoute(model = EipSceneDefinitionProxy.MODEL_MODEL) class EipSceneDefinitionProxyMenu {}
     * @UxMenu("场景实例") @UxRoute(model = EipSceneInstance.MODEL_MODEL) class EipSceneInstanceMenu {}
     * @UxMenu("组合接口") @UxRoute(model = EipRouteDefinition.MODEL_MODEL) class EipRouteDefinitionMenu {}
     * }
     **/

    @UxMenu("接口日志")
    @UxRoute(model = EipLogProxy.MODEL_MODEL, viewName = "EIP日志table")
    class EipLogMenu {
    }

    @UxMenu("基础数据")
    class EipBasicMenu {
        @UxMenu("业务域")
        @UxRoute(model = EipConnGroup.MODEL_MODEL, viewName = "EipConnGroupTable")
        class EipLogMenu {
        }
    }
}
package pro.shushi.pamirs.eip.api.util;

import org.apache.camel.model.RouteDefinition;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;

/**
 * @author drome
 * @date 2021/7/294:36 下午
 */
public class EipCamelRouteUtil extends EipCamelRouteBaseUtil {

    private EipCamelRouteUtil() {
        super();
    }

    protected static EipCamelRouteUtil newInstance(EipInitializationUtil initializationUtil, RouteDefinition processorDefinition) {
        EipCamelRouteUtil routeUtil = new EipCamelRouteUtil();
        routeUtil.initializationUtil = initializationUtil;
        routeUtil.processorDefinition = processorDefinition;
        return routeUtil;
    }

    public void end() {
        processorDefinition.end();
        processorDefinition.onException(Throwable.class)
                .handled(Boolean.TRUE)
                .process(EipFunctionConstant.DEFAULT_INTEGRATION_INTERFACE_ERROR_HANDLER);
        this.initializationUtil.addRouteDefinitionToContext((RouteDefinition) processorDefinition);
    }

    public EipInitializationUtil and() {
        end();
        return initializationUtil;
    }
}

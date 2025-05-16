package pro.shushi.pamirs.eip.api.util;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipFilter;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;

/**
 * @author drome
 * @date 2021/7/294:36 下午
 */
public class EipCamelRouteFilterUtil extends EipCamelRouteBaseUtil {

    private EipCamelRouteBaseUtil parent;

    private IEipFilter<SuperMap> filter;

    private EipCamelRouteFilterUtil() {
        super();
    }

    protected static EipCamelRouteFilterUtil newInstatnce(EipCamelRouteBaseUtil routeUtil, IEipFilter filter) {
        EipCamelRouteFilterUtil routeFilterUtil = new EipCamelRouteFilterUtil();
        routeFilterUtil.parent = routeUtil;
        routeFilterUtil.filter = filter;

        routeFilterUtil.initializationUtil = routeUtil.initializationUtil;
        routeFilterUtil.processorDefinition = routeFilterUtil.parent.processorDefinition.filter(exchange -> routeFilterUtil.filter.matches(EipInterfaceContext.getExecutorContext(exchange), (ExtendedExchange) exchange));
        return routeFilterUtil;
    }

    public EipCamelRouteBaseUtil and() {
        return this.parent;
    }
}

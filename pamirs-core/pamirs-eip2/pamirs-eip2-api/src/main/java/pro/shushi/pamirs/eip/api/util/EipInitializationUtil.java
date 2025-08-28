package pro.shushi.pamirs.eip.api.util;

import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.constant.EipConfigurationConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class EipInitializationUtil {

    private final EipCamelContext context;

    public static final String INTEGRATION_API_ID_PREFIX = "integration-api-";

    public static final String OPEN_API_ID_PREFIX = "openapi-";

    private EipInitializationUtil(EipCamelContext context) {
        this.context = context;
    }

    public static EipInitializationUtil newInstance() {
        return new EipInitializationUtil(EipCamelContext.getContext());
    }

    public static EipInitializationUtil newInstance(EipCamelContext context) {
        return new EipInitializationUtil(context);
    }

    private EipCamelRouteUtil from(String interfaceName) {
        return from(getEipInterfaceByContext(interfaceName), Boolean.FALSE);
    }

    public EipCamelRouteUtil from(IEipIntegrationInterface eipInterface) {
        return from(eipInterface, Boolean.TRUE);
    }

    private EipCamelRouteUtil from(IEipIntegrationInterface eipInterface, boolean isTemporary) {
        if (isTemporary) {
            verificationInterfaceNotNull(eipInterface);
            EipInterfaceContext.putTemporaryInterface(eipInterface);
        }
        return EipCamelRouteUtil.newInstance(this, buildRouteDefinition(generatorIntegrationInterfaceRouteId(eipInterface.getInterfaceName()), eipInterface.getUri()));
    }

    public RouteDefinition removeIntegrationInterface(String interfaceName) {
        return removeRouteDefinitionToContext(generatorIntegrationInterfaceRouteId(interfaceName));
    }

    public static String generatorIntegrationInterfaceRouteId(String interfaceName) {
        return INTEGRATION_API_ID_PREFIX + interfaceName;
    }

    public EipInitializationUtil addOpenApi(IEipOpenInterface eipOpenApi) {
        return addOpenApi(eipOpenApi, null);
    }

    public EipInitializationUtil addOpenApi(IEipOpenInterface eipOpenApi, Consumer<RouteDefinition> consumer) {
        RouteDefinition routeDefinition = new RouteDefinition();
        String uri = eipOpenApi.getUri();
        if (uri.startsWith(EipConfigurationConstant.ENDPOINT_REST)) {
            routeDefinition.fromRest(uri);
        } else if (uri.startsWith(EipConfigurationConstant.STREAM_URI_PREFIX_MARK)) {
            routeDefinition.from(uri.substring(EipConfigurationConstant.STREAM_URI_PREFIX_MARK.length()) + "?httpMethodRestrict=POST&streaming=true");
            routeDefinition.noStreamCaching();
        } else {
            routeDefinition.from(uri);
        }
        routeDefinition.setId(generatorOpenApiRouteId(eipOpenApi.getInterfaceName()));
        routeDefinition.process(eipOpenApi.getProcessor());
        if (consumer != null) {
            consumer.accept(routeDefinition);
        }
        routeDefinition.end();
        routeDefinition.onException(Throwable.class)
                .handled(true)
                .process(eipOpenApi.getErrorHandler());
        addRouteDefinitionToContext(routeDefinition);
        return this;
    }

    public RouteDefinition removeOpenApi(String interfaceName) {
        return removeRouteDefinitionToContext(generatorOpenApiRouteId(interfaceName));
    }

    public static String parseInterfaceNameByRouteId(String routeId) {
        if (routeId == null) {
            return null;
        }
        if (routeId.startsWith(INTEGRATION_API_ID_PREFIX)) {
            return routeId.substring(INTEGRATION_API_ID_PREFIX.length());
        }
        if (routeId.startsWith(OPEN_API_ID_PREFIX)) {
            return routeId.substring(OPEN_API_ID_PREFIX.length());
        }
        return routeId;
    }

    public static String generatorOpenApiRouteId(String interfaceName) {
        return OPEN_API_ID_PREFIX + interfaceName;
    }

    @Deprecated
    public EipInitializationUtil addRouteDefinition(Consumer<RouteDefinition> consumer) {
        RouteDefinition routeDefinition = new RouteDefinition();
        consumer.accept(routeDefinition);
        addRouteDefinitionToContext(routeDefinition);
        return this;
    }

    protected static final BiConsumer<IEipIntegrationInterface, ProcessorDefinition<?>> TO_ROUTE_DEFINITION = (eipInterface, routeDefinition) -> {
        if (eipInterface.getIsDynamic()) {
            routeDefinition.toD(eipInterface.getUri(), eipInterface.getDynamicProtocolCacheSize());
        } else {
            routeDefinition.to(eipInterface.getExchangePattern().getExchangePattern(), eipInterface.getUri());
        }
    };

    private static void verificationInterfaceNotNull(IEipIntegrationInterface eipInterface) {
        if (eipInterface == null) {
            throw new IllegalArgumentException("指定接口不允许为空");
        }
    }

    protected static IEipIntegrationInterface getEipInterfaceByContext(String interfaceName) {
        IEipIntegrationInterface eipInterface = EipInterfaceContext.getAnyInterface(interfaceName);
        if (eipInterface == null) {
            throw new IllegalArgumentException(String.format("未找到指定接口 [InterfaceName %s]", interfaceName));
        }
        return eipInterface;
    }

    private static RouteDefinition buildRouteDefinition(String id, String uri) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.from(uri);
        routeDefinition.setId(id);
        return routeDefinition;
    }

    protected void addRouteDefinitionToContext(RouteDefinition routeDefinition) {
        addRouteDefinitionToContext(context, routeDefinition);
    }

    protected void addRouteDefinitionToContext(EipCamelContext context, RouteDefinition routeDefinition) {
        String routeId = routeDefinition.getId();
        assertContext(context, routeId);
        try {
            context.getCamelContext().addRouteDefinition(routeDefinition);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("路由定义不合法 [interfaceName %s]", routeId), e);
        }
    }

    protected RouteDefinition removeRouteDefinitionToContext(String routeId) {
        return removeRouteDefinitionToContext(context, routeId);
    }

    protected RouteDefinition removeRouteDefinitionToContext(EipCamelContext context, String routeId) {
        assertContext(context, routeId);
        ModelCamelContext camelContext = context.getCamelContext();
        try {
            RouteDefinition routeDefinition = camelContext.getRouteDefinition(routeId);
            if (routeDefinition != null) {
                camelContext.removeRouteDefinition(routeDefinition);
            }
            return routeDefinition;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("路由移除失败 [interfaceName %s]", routeId), e);
        }
    }

    private static void assertContext(EipCamelContext context, String routeId) {
        if (context == null) {
            throw new IllegalArgumentException(String.format("未初始化Camel上下文 [interfaceName %s]", routeId));
        }
    }

    protected void temporaryRouteDefinitionProcessor(IEipIntegrationInterface eipInterface) {
        verificationInterfaceNotNull(eipInterface);
        from(EipFunctionConstant.EMPTY.apply(eipInterface.getContext(), eipInterface.getInterfaceName()), false)
                .<EipCamelRouteUtil>to(eipInterface, Boolean.FALSE, TO_ROUTE_DEFINITION)
                .end();
    }
}

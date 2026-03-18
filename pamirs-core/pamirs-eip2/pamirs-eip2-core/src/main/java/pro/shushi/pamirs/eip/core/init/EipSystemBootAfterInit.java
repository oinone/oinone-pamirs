package pro.shushi.pamirs.eip.core.init;

import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.auth.OpenApiConstant;
import pro.shushi.pamirs.eip.api.camel.IEipRegister;
import pro.shushi.pamirs.eip.api.camel.RegistryComponentBody;
import pro.shushi.pamirs.eip.api.config.EipSwitchCondition;
import pro.shushi.pamirs.eip.api.config.PamirsEipOpenApiProperties;
import pro.shushi.pamirs.eip.api.config.PamirsEipProperties;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import javax.net.ssl.HostnameVerifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

@Slf4j
@Order(0)
@Component
@Conditional(EipSwitchCondition.class)
public class EipSystemBootAfterInit implements SystemBootAfterInit {

    @Autowired
    private PamirsEipProperties eipProperties;

    @Autowired(required = false)
    private PamirsEipOpenApiProperties openApiConfiguration;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean init(AppLifecycleCommand command) {
        EipCamelContext camelContext = EipCamelContext.getContext();
        setDefaultCamelProperties(camelContext);
        if (openApiConfiguration != null) {
            PamirsEipOpenApiProperties.RouteConfigurationItem routeConfigurationItem = openApiConfiguration.getRoute();
            if (routeConfigurationItem != null) {
                setRestConfiguration(camelContext, routeConfigurationItem);
                initAccessTokenOpenApi();
                if (openApiConfiguration.getTest()) {
                    initTestOpenApi();
                }
            }
        }
        return true;
    }

    private void setRestConfiguration(EipCamelContext context, PamirsEipOpenApiProperties.RouteConfigurationItem routeConfigurationItem) {
        RestConfiguration restConfiguration = new RestConfiguration();
        String host = routeConfigurationItem.getHost();
        restConfiguration.setHost(host);
        restConfiguration.setPort(routeConfigurationItem.getPort());
        context.getCamelContext().setRestConfiguration(restConfiguration);
    }

    private void setDefaultCamelProperties(EipCamelContext context) {
        HttpComponent httpComponent = context.getCamelContext().getComponent("http", HttpComponent.class);
        if (httpComponent != null) {
            setHttpComponentProperties(httpComponent, eipProperties.getHttp());
        }
        HttpComponent httpsComponent = context.getCamelContext().getComponent("https", HttpComponent.class);
        if (httpsComponent != null) {
            setHttpComponentProperties(httpsComponent, eipProperties.getHttp());
        }

        ModelCamelContext camelContext = context.getCamelContext();
        Registry registry = camelContext.getRegistry();
        for (IEipRegister register : BeanDefinitionUtils.getBeansOfTypeByOrdered(IEipRegister.class)) {
            List<RegistryComponentBody> list = register.registers();
            if (CollectionUtils.isNotEmpty(list)) {
                for (RegistryComponentBody item : list) {
                    Object bean = item.getBean();
                    if (bean instanceof org.apache.camel.Component) {
                        camelContext.addComponent(item.getId(), cast(bean));
                    } else {
                        Class<?> clazz = item.getClazz();
                        if (clazz == null) {
                            registry.bind(item.getId(), item.getBean());
                        } else {
                            registry.bind(item.getId(), clazz, item.getBean());
                        }
                    }
                }
            }
        }
    }

    private void setHttpComponentProperties(HttpComponent httpComponent, PamirsEipProperties.Http properties) {
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setHttpClientConfigurer, HttpClientConfigurer.class);
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setClientConnectionManager, HttpClientConnectionManager.class);
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setHttpContext, HttpContext.class);
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setSslContextParameters, SSLContextParameters.class);
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setX509HostnameVerifier, HostnameVerifier.class);
        setDefaultBeanConfiguration(httpComponent, HttpComponent::setCookieStore, CookieStore.class);
        httpComponent.setConnectionRequestTimeout(properties.getConnectionRequestTimeout());
        httpComponent.setConnectTimeout(properties.getConnectTimeout());
        httpComponent.setSocketTimeout(properties.getSocketTimeout());
        httpComponent.setMaxTotalConnections(properties.getMaxTotalConnections());
        httpComponent.setConnectionsPerRoute(properties.getConnectionsPerRoute());
        httpComponent.setConnectionTimeToLive(properties.getConnectionTimeToLive());
    }

    private void initAccessTokenOpenApi() {
        List<EipOpenInterface> eipOpenInterfaceList = new ArrayList<>();
        eipOpenInterfaceList.add(new EipOpenInterface()
                .setConverterNamespace(EipFunctionConstant.FUNCTION_NAMESPACE)
                .setConverterFun(EipFunctionConstant.DEFAULT_OPEN_API_GET_ACCESS_TOKEN_FUN)
                .setUri("rest:post:openapi/get/access-token")
                .setModule(EipModule.MODULE_MODULE)
                .setName(I18nUtils.getMessage("eip.core.init.access.token"))
                .setInterfaceName(OpenApiConstant.OPEN_API_GET_ACCESS_TOKEN)
                .construct());
        FetchUtil.onlyCreateBatch(eipOpenInterfaceList);
    }

    private void initTestOpenApi() {
        List<EipOpenInterface> eipOpenInterfaceList = new ArrayList<>();
        String uri = "rest:get:openapi/test/get";
        eipOpenInterfaceList.add(new EipOpenInterface()
                .setAuthenticationProcessorNamespace(EipFunctionConstant.FUNCTION_NAMESPACE)
                .setAuthenticationProcessorFun(EipFunctionConstant.DEFAULT_AUTHENTICATION_PROCESSOR_FUN)
                .setUri(uri)
                .setModule(EipModule.MODULE_MODULE)
                .setName(I18nUtils.getMessage("eip.core.init.test.get.encrypt"))
                .setInterfaceName("test_get_open_api")
                .construct());

        uri = "rest:post:openapi/test/post";
        eipOpenInterfaceList.add(new EipOpenInterface()
                .setAuthenticationProcessorNamespace(EipFunctionConstant.FUNCTION_NAMESPACE)
                .setAuthenticationProcessorFun(EipFunctionConstant.DEFAULT_AUTHENTICATION_PROCESSOR_FUN)
                .setUri(uri)
                .setModule(EipModule.MODULE_MODULE)
                .setName(I18nUtils.getMessage("eip.core.init.test.post.encrypt"))
                .setInterfaceName("test_post_open_api")
                .construct());

        uri = "rest:get:openapi/test/signature/get";
        eipOpenInterfaceList.add(new EipOpenInterface()
                .setAuthenticationProcessorNamespace(EipFunctionConstant.FUNCTION_NAMESPACE)
                .setAuthenticationProcessorFun(EipFunctionConstant.DEFAULT_MD5_SIGNATURE_AUTHENTICATION_PROCESSOR_FUN)
                .setUri(uri)
                .setModule(EipModule.MODULE_MODULE)
                .setName(I18nUtils.getMessage("eip.core.init.test.get.sign"))
                .setInterfaceName("test_get_signature_open_api")
                .construct());

        uri = "rest:post:openapi/test/signature/post";
        eipOpenInterfaceList.add(new EipOpenInterface()
                .setAuthenticationProcessorNamespace(EipFunctionConstant.FUNCTION_NAMESPACE)
                .setAuthenticationProcessorFun(EipFunctionConstant.DEFAULT_MD5_SIGNATURE_AUTHENTICATION_PROCESSOR_FUN)
                .setUri(uri)
                .setModule(EipModule.MODULE_MODULE)
                .setName(I18nUtils.getMessage("eip.core.init.test.post.sign"))
                .setInterfaceName("test_post_signature_open_api")
                .construct());
        FetchUtil.onlyCreateBatch(eipOpenInterfaceList);
    }

    private <T, V> void setDefaultBeanConfiguration(T object, BiConsumer<T, V> setter, Class<V> cls) {
        List<V> beanList = BeanDefinitionUtils.getBeansOfTypeByOrdered(cls);
        if (CollectionUtils.isNotEmpty(beanList)) {
            setter.accept(object, beanList.get(0));
        }
    }
}

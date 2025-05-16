package pro.shushi.pamirs.eip;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.spi.RestConfiguration;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConverter;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.builder.DefaultEipInterfaceBuilder;
import pro.shushi.pamirs.eip.api.builder.DefaultEipOpenApiBuilder;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.DefaultEipConvertParam;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.util.EipInitializationUtil;

/**
 * @author Adamancy Zhang at 14:29 on 2024-03-20
 */
public class CamelTest {

    public static void main(String[] args) throws Exception {
//        openApiTest();
        sqlTest();
    }

    private static void openApiTest() throws Exception {
        EipCamelContext context = EipCamelContext.getContext();

        RestConfiguration restConfiguration = new RestConfiguration();
        String host = "192.168.0.123";
        restConfiguration.setHost(host);
        restConfiguration.setPort(9099);
        context.getCamelContext().setRestConfiguration(restConfiguration);

        EipInterfaceContext.putInterface(DefaultEipInterfaceBuilder.newInstance(context, "test1", "http://192.168.0.123:9092/request/{id}")
                .createRequestParamProcessor()
                .setFinalResultKey("empty")
                .addConvertParam(new DefaultEipConvertParam<>("id", IEipContext.URL_DYNAMIC_PARAMS_KEY + ".id"))
                .and()
                .createResponseParamProcessor()
                .and()
                .createExceptionParamProcessor()
                .and()
                .build());

        EipInitializationUtil.newInstance().addOpenApi(DefaultEipOpenApiBuilder.newInstance(context, "openTest1", "rest:get:openapi/request/{id}")
                .setConverter(new IEipConverter<SuperMap>() {
                    @Override
                    public void convert(IEipContext<SuperMap> context, ExtendedExchange exchange) {
                        EipResult<SuperMap> result = EipInterfaceContext.call("test1", MapHelper.newInstance()
                                .put("id", exchange.getMessage().getHeader("id"))
                                .build());
                        context.putInterfaceContextValue("result", result);
                    }
                })
                .setFinalResultKey("result")
                .build());

        EipInterfaceContext.routeInitialization(context);

        context.start();

        EipResult<SuperMap> result = EipInterfaceContext.call("test1", MapHelper.newInstance()
                .put("id", "123")
                .build());


        System.out.println(result.getSuccess());

        // Sleeping until the end of time
        Thread.sleep(100000000000000000L);

        context.stop();
    }

    private static void sqlTest() throws Exception {
        EipCamelContext context = EipCamelContext.getContext();

        EipInterfaceContext.putInterface(DefaultEipInterfaceBuilder.newInstance(context, "testSQL", "sql:{sql}?dataSource=testDB")
                .createRequestParamProcessor()
                .setFinalResultKey("empty")
                .addConvertParam(new DefaultEipConvertParam<>("sql", IEipContext.URL_DYNAMIC_PARAMS_KEY + ".sql"))
                .and()
                .createResponseParamProcessor()
                .setInOutConverter(new IEipInOutConverter() {
                    @Override
                    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
                        System.out.println(1);
                        return inObject;
                    }
                })
                .and()
                .createExceptionParamProcessor()
                .and()
                .build());

        EipInterfaceContext.routeInitialization(context);

        context.start();

        EipResult<SuperMap> result = EipInterfaceContext.call("testSQL", MapHelper.newInstance()
                .put("sql", "select * from ts1")
                .build());


        System.out.println(result.getSuccess());

        // Sleeping until the end of time
        Thread.sleep(100000000000000000L);

        context.stop();
    }
}

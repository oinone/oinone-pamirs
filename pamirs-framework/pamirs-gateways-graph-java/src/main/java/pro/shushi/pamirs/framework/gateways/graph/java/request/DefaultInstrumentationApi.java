package pro.shushi.pamirs.framework.gateways.graph.java.request;

import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.configuration.PamirsFrameworkGatewayConfiguration;
import pro.shushi.pamirs.framework.gateways.graph.instrument.ClientDataInstrumentation;
import pro.shushi.pamirs.framework.gateways.graph.instrument.MessageHubInstrumentation;
import pro.shushi.pamirs.framework.gateways.graph.spi.InstrumentationApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器spi实现
 * <p>
 * 2021/3/26 7:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
@SPI.Service
public class DefaultInstrumentationApi implements InstrumentationApi {

    @Resource
    private PamirsFrameworkGatewayConfiguration pamirsFrameworkGatewayConfiguration;

    @Override
    public ChainedInstrumentation build() {
        DataLoaderDispatcherInstrumentationOptions options = DataLoaderDispatcherInstrumentationOptions
                .newOptions().includeStatistics(pamirsFrameworkGatewayConfiguration.isStatistics());

        DataLoaderDispatcherInstrumentation dispatcherInstrumentation
                = new DataLoaderDispatcherInstrumentation(options);

        // 拦截器
        List<Instrumentation> chainedList = new ArrayList<>();
        chainedList.add(new MessageHubInstrumentation());
        chainedList.add(new ClientDataInstrumentation());
        chainedList.add(dispatcherInstrumentation);
        return new ChainedInstrumentation(chainedList);
    }

}

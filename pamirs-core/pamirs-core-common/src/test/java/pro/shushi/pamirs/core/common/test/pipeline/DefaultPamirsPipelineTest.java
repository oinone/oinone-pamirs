package pro.shushi.pamirs.core.common.test.pipeline;

import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.extension.DefaultPamirsPipeline;

public class DefaultPamirsPipelineTest extends DefaultPamirsPipeline {

    public DefaultPamirsPipelineTest(Feature... features) {
        super(features);
    }

    public DefaultPamirsPipelineTest(String signature, Feature... features) {
        super(signature, features);
    }

    @Override
    public PamirsExchange invoke(PamirsExchange exchange) {
        String signature = signature();
        System.out.printf("开始执行管道 %s\n", signature);
        super.invoke(exchange);
        System.out.printf("结束执行管道 %s\n", signature);
        return exchange;
    }
}
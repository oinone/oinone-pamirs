package pro.shushi.pamirs.core.common.test.runner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;
import pro.shushi.pamirs.core.common.test.pipeline.DefaultPamirsPipelineTest;
import pro.shushi.pamirs.core.common.test.valve.PamirsChooseValveTest;
import pro.shushi.pamirs.core.common.test.valve.PamirsFilterValveTest;
import pro.shushi.pamirs.core.common.test.valve.PamirsLoopValveTest;
import pro.shushi.pamirs.core.common.test.valve.PamirsValveTest;
import pro.shushi.pamirs.core.common.pipeline.PamirsChooseValve;
import pro.shushi.pamirs.core.common.pipeline.PamirsExchange;
import pro.shushi.pamirs.core.common.pipeline.PamirsPipeline;
import pro.shushi.pamirs.core.common.pipeline.PamirsValve;
import pro.shushi.pamirs.core.common.pipeline.constant.PamirsPipelineConstant;
import pro.shushi.pamirs.core.common.pipeline.extension.DefaultPamirsExchange;

/**
 * @author Adamancy Zhang on 2021-05-09 12:15
 */
@DisplayName("阀基础功能实现测试")
public class PamirsValveBaseTest extends AbstractBaseTest {

    @DisplayName("选择阀测试-选择基础阀直接执行")
    @Test
    public void pamirsChooseValveTest1() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest();
        pipeline.addValve(new PamirsChooseValveTest("choose valve"));

        PamirsExchange exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsChooseValveTest.KEY, new PamirsValveTest("valve"));
        pipeline.invoke(exchange);
    }

    @DisplayName("选择阀测试-选择自己直接执行")
    @Test
    public void pamirsChooseValveTest2() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest();
        PamirsChooseValve chooseValve = new PamirsChooseValveTest("choose valve");
        pipeline.addValve(chooseValve);

        PamirsExchange exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsChooseValveTest.KEY, chooseValve);
        pipeline.invoke(exchange);
    }

    @DisplayName("选择阀测试-循环选择超过限制次数")
    @Test
    public void pamirsChooseValveTest3() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest();
        pipeline.addValve(new PamirsChooseValveTest("choose valve"));

        PamirsExchange exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsChooseValveTest.KEY, new PamirsChooseValveTest("choose valve") {
            @Override
            public PamirsValve choose(PamirsExchange exchange) {
                return new PamirsChooseValveTest("choose valve");
            }
        });
        pipeline.invoke(exchange);
        boolean isInterrupt = exchange.isInterrupted();
        assert isInterrupt : "pipeline execution is not interrupted";
        Throwable throwable = exchange.getThrowable();
        assert throwable instanceof IllegalArgumentException : "pipeline execution exception mismatch";
        assert ("The choose valve has not been executed for more than " + PamirsPipelineConstant.MAX_CHOOSE_NUMBER + " times. Force Interrupted.").equals(throwable.getMessage()) : throwable.getMessage();
    }

    @DisplayName("过滤阀测试")
    @Test
    public void pamirsFilterValveTest() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest();
        pipeline.addValve(new PamirsFilterValveTest("filter valve"));

        PamirsExchange exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsFilterValveTest.KEY, true);
        pipeline.invoke(exchange);

        exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsFilterValveTest.KEY, false);
        pipeline.invoke(exchange);
    }

    @DisplayName("循环阀测试")
    @Test
    public void pamirsLoopValveTest() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest();
        pipeline.addValve(new PamirsLoopValveTest("loop valve"));

        PamirsExchange exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsLoopValveTest.KEY, true);
        pipeline.invoke(exchange);

        exchange = new DefaultPamirsExchange();
        exchange.setProperty(PamirsLoopValveTest.KEY, false);
        pipeline.invoke(exchange);
    }
}

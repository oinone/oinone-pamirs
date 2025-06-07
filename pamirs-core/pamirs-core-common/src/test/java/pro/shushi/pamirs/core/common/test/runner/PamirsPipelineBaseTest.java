package pro.shushi.pamirs.core.common.test.runner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.LetterHelper;
import pro.shushi.pamirs.core.common.pipeline.PamirsPipeline;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;
import pro.shushi.pamirs.core.common.test.pipeline.DefaultPamirsPipelineTest;
import pro.shushi.pamirs.core.common.test.valve.PamirsValveTest;

import java.util.function.Supplier;

/**
 * @author Adamancy Zhang on 2021-05-09 10:23
 */
@DisplayName("管道基础功能实现测试")
public class PamirsPipelineBaseTest extends AbstractBaseTest {

    @DisplayName("默认管道测试")
    @Test
    public void defaultPamirsPipelineTest() {
        test0(() -> new DefaultPamirsPipelineTest(LetterHelper.getRandomString(12)));
    }

    @DisplayName("串行管道测试")
    @Test
    public void serialPamirsPipelineTest() {
        test0(() -> new DefaultPamirsPipelineTest(LetterHelper.getRandomString(12), PamirsPipeline.Feature.SERIAL));
    }

    @DisplayName("不可重复阀管道测试")
    @Test
    public void nonRepeatValvePamirsPipelineTest() {
        PamirsPipeline pipeline = new DefaultPamirsPipelineTest("pipeline 1", PamirsPipeline.Feature.NON_REPEAT_VALVE);
        pipeline.addValve(new PamirsValveTest("valve 1"));
        try {
            pipeline.addValve(new PamirsValveTest("valve 1"));
        } catch (IllegalArgumentException e) {
            assert "Valve signature is repeat. signature=valve 1".equals(e.getMessage()) : e.getMessage();
        }
        PamirsPipeline child1Pipeline = new DefaultPamirsPipelineTest("pipeline 1");
        try {
            pipeline.include(child1Pipeline);
        } catch (IllegalArgumentException e) {
            assert "Pipeline signature is repeat. signature=pipeline 1".equals(e.getMessage()) : e.getMessage();
        }
        System.out.println("测试通过");
    }

    private void test0(Supplier<PamirsPipeline> supplier) {
        PamirsPipeline pipeline = supplier.get();
        pipeline.addValve(new PamirsValveTest("valve 1"));
        pipeline.addValve(new PamirsValveTest("valve 2"));
        pipeline.addValve(new PamirsValveTest("valve 3"));

        PamirsPipeline child1Pipeline = supplier.get();
        child1Pipeline.addValve(new PamirsValveTest("valve 4"));
        child1Pipeline.addValve(new PamirsValveTest("valve 5"));
        child1Pipeline.addValve(new PamirsValveTest("valve 6"));

        pipeline.include(child1Pipeline);

        pipeline.addValve(new PamirsValveTest("valve 7"));
        pipeline.addValve(new PamirsValveTest("valve 8"));
        pipeline.addValve(new PamirsValveTest("valve 9"));

        PamirsPipeline child2Pipeline = supplier.get();
        child2Pipeline.addValve(new PamirsValveTest("valve 10"));
        child2Pipeline.addValve(new PamirsValveTest("valve 11"));
        child2Pipeline.addValve(new PamirsValveTest("valve 12"));

        pipeline.include(child2Pipeline);

        pipeline.addValve(new PamirsValveTest("valve 13"));
        pipeline.addValve(new PamirsValveTest("valve 14"));
        pipeline.addValve(new PamirsValveTest("valve 15"));

        pipeline.invoke(null);
    }
}
